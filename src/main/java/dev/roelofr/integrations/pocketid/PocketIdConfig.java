package dev.roelofr.integrations.pocketid;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app.services.pocket-id")
public interface PocketIdConfig {
    boolean enabled();

    String url();

    String apiKey();
}
