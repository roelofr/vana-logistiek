package dev.roelofr.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.nio.file.Path;

@ConfigMapping(prefix = "app")
public interface AppConfig {
    String version();

    @WithName("seed")
    @WithDefault("false")
    boolean shouldSeed();

    AppFolders folders();

    AppRoles roles();

    interface AppFolders {
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
