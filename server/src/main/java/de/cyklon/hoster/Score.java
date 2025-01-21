package de.cyklon.hoster;

import lombok.Data;

@Data
public class Score {

    private final double uptime;
    private final double ramAccessibility;
    private final double cpuAccessibility;


    public double calculateScore() {
        return (cpuAccessibility * ramAccessibility) * uptime;
    }

}
