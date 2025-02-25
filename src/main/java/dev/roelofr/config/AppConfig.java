package dev.roelofr.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "app")
public interface AppConfig {
    String version();

    @WithName("seed")
    @WithDefault("false")
    boolean shouldSeed();
}
