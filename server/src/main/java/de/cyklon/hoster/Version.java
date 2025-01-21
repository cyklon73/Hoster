package de.cyklon.hoster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.cyklon.hoster.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Version {

    public static final Map<String, String> VANILLA;

    public static final Map<String, String> CRAFTBUKKIT;

    public static final Map<String, String> SPIGOT;

    public static final Map<String, String> PAPER;

    static {
        FirefoxDriver driver = new FirefoxDriver();
        VANILLA = readGetBukkitOrg(driver, "vanilla");
        CRAFTBUKKIT = readGetBukkitOrg(driver, "craftbukkit");
        SPIGOT = readGetBukkitOrg(driver, "spigot");
        PAPER = readPaper();
        driver.close();
    }

    private static Map<String, String> readPaper() {
        // Fetch the main project data
        JsonObject projectData = JsonUtil.fromUrl("https://api.papermc.io/v2/projects/paper");
        JsonArray versions = projectData.getAsJsonArray("versions");

        // Fetch detailed version data
        Map<String, String> versionUrls = new LinkedHashMap<>();
        for (int i = versions.size() - 1; i >= 0; i--) {
            String version = versions.get(i).getAsString();
            JsonObject versionData = JsonUtil.fromUrl(
                    "https://api.papermc.io/v2/projects/paper/versions/" + version);

            String latestBuildNumber = versionData.getAsJsonArray("builds")
                    .get(versionData.getAsJsonArray("builds").size() - 1)
                    .getAsString();

            String downloadUrl = String.format(
                    "https://api.papermc.io/v2/projects/paper/versions/%s/builds/%s/downloads/paper-%s-%s.jar",
                    version, latestBuildNumber, version, latestBuildNumber);

            versionUrls.put(version, downloadUrl);
        }
        return versionUrls;
    }

    private static Map<String, String> readGetBukkitOrg(RemoteWebDriver driver, String type) {
        Map<String, String> versionUrls = new LinkedHashMap<>();
        driver.get("https://getbukkit.org/download/" + type + "/version");

        List<WebElement> elements = driver.findElement(By.id("download")).findElement(By.cssSelector("col-md-12 download")).findElements(By.className("download-pane"));

        for (WebElement element : elements) {
            element = element.findElement(By.cssSelector("row vdivide"));
            String version = null, url = null;
            for (WebElement e : element.findElements(By.cssSelector("*"))) {
                List<WebElement> h4 = e.findElements(By.tagName("h4"));
                if (!h4.isEmpty()) {
                    String label = h4.get(0).getDomAttribute("innerText");
                    if ("version".equalsIgnoreCase(label)) version = e.findElement(By.tagName("h2")).getDomAttribute("innerText");
                } else {
                    List<WebElement> btnGroup = e.findElements(By.className("btn-group"));
                    if (!btnGroup.isEmpty()) {
                        url = btnGroup.get(0).findElement(By.className("btn-download")).getDomAttribute("href");
                    }
                }
            }
            if (version != null && url != null) versionUrls.put(version, url);
        }
        return versionUrls;
    }
}
