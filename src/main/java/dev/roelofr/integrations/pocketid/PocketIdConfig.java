package dev.roelofr.integrations.pocketid;

import io.smallrye.config.ConfigMapping;

/**
 * Configuration interface for the PocketID integration.
 */
@ConfigMapping(prefix = "app.services.pocket-id")
public interface PocketIdConfig {
    /**
     * Should the PocketID Service be enabled?
     *
     * @return true if enabled, false otherwise
     */
    boolean enabled();

    /**
     * The base URL for the PocketID service.
     *
     * @return the base URL
     */
    String url();

    /**
     * The API key for the PocketID service.
     *
     * @return the API key
     */
    String apiKey();
}
