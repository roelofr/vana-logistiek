package dev.roelofr.service;

import dev.roelofr.config.AppConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static dev.roelofr.AppUtil.getExtension;

@Slf4j
@ApplicationScoped
public class FileService {
    public static int BYTES_PER_WRITE = 10240;
    public static long MAX_UPLOAD_FILESIZE = 15 * (2 ^ 10) ^ 3; // 15 MiB
    private final Path uploadFolder;

    public FileService(AppConfig appConfig) {
        uploadFolder = appConfig.folders().uploads();
    }

    public Path persistUpload(FileUpload upload) {
        if (upload.size() > MAX_UPLOAD_FILESIZE) {
            log.error("Filesize {} is exceeding mxa size {}", upload.size(), MAX_UPLOAD_FILESIZE);
//            throw new IllegalArgumentException("Filesize too large");
        }

        var sourcePath = upload.uploadedFile();
        var targetPath = uploadFolder.resolve(String.format("./upload-%s.%s", UUID.randomUUID(), getExtension(upload.fileName()))).toAbsolutePath();

        var sourceName = sourcePath.getFileName().toString();
        var targetName = targetPath.getFileName().toString();

        log.info("File {} will be uploaded to {}", sourceName, targetName);

        try (
            var sourceStream = new FileInputStream(sourcePath.toFile());
            var targetStream = new FileOutputStream(targetPath.toFile())
        ) {
            do {
                // Throttle read all bytes
                targetStream.write(
                    sourceStream.readNBytes(Math.min(BYTES_PER_WRITE, sourceStream.available()))
                );
            } while (sourceStream.available() > 0);

            targetStream.write(sourceStream.readAllBytes());

            return targetPath;
        } catch (FileNotFoundException exception) {
            throw new RuntimeException(
                exception.getMessage().contains(sourceName)
                    ? "Uploaded file could not be found"
                    : "Destination file could not be created, or was not writeable",
                exception);
        } catch (IOException exception) {
            throw new RuntimeException(String.format(
                "Failed to process file: %s %s",
                exception.getClass().getSimpleName(),
                exception.getMessage()
            ), exception);
        }
    }
}
