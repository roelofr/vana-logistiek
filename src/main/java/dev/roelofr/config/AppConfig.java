package dev.roelofr.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.nio.file.Path;

/**
 * Application configuration of the Penis LogistiekApp.
 */
@ConfigMapping(prefix = "app")
public interface AppConfig {
    /**
     * Application version
     */
    @WithName("version")
    String version();

    /**
     * Should the database be seeded with dummy data?
     */
    @WithName("seed")
    @WithDefault("false")
    boolean shouldSeed();

    /**
     * Folder storage configuration
     */
    AppFolders folders();

    /**
     * Application roles configuration
     */
    AppRoles roles();

    interface AppFolders {
        /**
         * Location where the uploads will be stored.
         */
        @WithDefault("./uploads")
        Path uploads();
    }

    interface AppRoles {
        /**
         * All app users
         */
        @WithDefault("user")
        String user();

        /**
         * Admins
         */
        @WithDefault("admin")
        String admin();

        /**
         * Centrale Post.
         */
        @WithDefault("cp")
        String centralePost();

        /**
         * Wijkhouders in het veld.
         */
        @WithDefault("wijkhouder")
        String wijkhouder();

        /**
         * Gebruikers die een rol kunnen spelen in bestaande tickets.
         */
        @WithDefault("role_gedelegeerd")
        String gedelegeerd();
    }
}
