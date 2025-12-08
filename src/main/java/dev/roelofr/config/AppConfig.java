package dev.roelofr.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.Map;

@ConfigMapping(prefix = "app")
public interface AppConfig {
    String version();

    @WithName("seed")
    @WithDefault("false")
    boolean shouldSeed();

    AppRoles roles();

    interface AppRoles {
        @WithDefault("admin")
        String admin();

        @WithDefault("cp")
        String centralePost();

        @WithDefault("wijkhouder")
        String wijkhouder();
    }
}
