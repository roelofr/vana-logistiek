package dev.roelofr.integrations.pocketid;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "app.pocket-id")
public interface PocketIdConfig {
    @WithDefault("false")
    boolean enabled();

    @WithDefault("https://example.com")
    String url();

    @WithDefault("my-api-key")
    String apiKey();
}
