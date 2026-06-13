package dev.roelofr.integrations.pocketid;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Optional;

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
    @WithDefault("false")
    boolean enabled();

    /**
     * The base URL for the PocketID service.
     *
     * @return the base URL
     */
    @WithDefault("https://example.com")
    Optional<String> url();

    /**
     * The API key for the PocketID service.
     *
     * @return the API key
     */
    @WithDefault("example-token")
    Optional<String> apiKey();
}
