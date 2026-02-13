package dev.roelofr.jobs.startup;

import dev.roelofr.config.AppConfig;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnsureStorageDirectoryExists {
    private final AppConfig appConfig;

    @Startup
    void ensureStorageFoldersExist() {
        ensureStorageFolderExists(appConfig.folders().uploads());
    }

    void ensureStorageFolderExists(Path folder) {
        var folderFile = folder.toAbsolutePath().toFile();
        var folderName = folderFile.toString();
        if (folderFile.exists() && folderFile.isDirectory())
            return;

        if (folderFile.exists())
            throw new RuntimeException(String.format("Wanted a folder named %s, got type %s instead", folderName, folderFile));

        log.info("Folder {} is missing, creating it.", folderName);

        var created = folderFile.mkdirs();
        if (!created)
            throw new RuntimeException(String.format("Failed to create %s or one or more of its parents", folderName));

        log.info("Created folder {}", folderName);
    }
}
