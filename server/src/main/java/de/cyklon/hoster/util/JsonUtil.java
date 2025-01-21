package de.cyklon.hoster.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

@UtilityClass
public class JsonUtil {

    public JsonObject fromUrl(String url) {
        try (InputStream in = new URL(url).openStream()) {
            return JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
