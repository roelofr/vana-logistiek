package dev.roelofr.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app")
public interface AppConfig {
    public String version();
}
