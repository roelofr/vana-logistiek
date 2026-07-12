package dev.roelofr.service;

import dev.roelofr.config.AppConfig;
import dev.roelofr.domains.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.jboss.resteasy.reactive.multipart.FilePart;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class FileService {
    public static int BYTES_PER_WRITE = 10240;
    public static long MAX_FILESIZE = 15 * 1048576; // 15 MiB
    private final Path uploadFolder;

    public FileService(AppConfig appConfig) {
        uploadFolder = appConfig.folders().uploads();
    }

    public String getFileMime(Path path) {
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            log.error("Failed to determine MIME type for file: {}", path.getFileName(), e);
            return null;
        }
    }

    public String getFileMime(File file) {
        return getFileMime(file.toPath());
    }

    Path persistFile(File sourceFile, File targetFile) {
        var sourceName = sourceFile.getName();
        var targetName = targetFile.getName();

        log.info("File {} will be stored as {}", sourceName, targetName);

        ensureWriteable(targetFile);

        try (
            var sourceStream = new FileInputStream(sourceFile);
            var targetStream = new FileOutputStream(targetFile)
        ) {
            do {
                // Throttle read all bytes
                targetStream.write(
                    sourceStream.readNBytes(Math.min(BYTES_PER_WRITE, sourceStream.available()))
                );
            } while (sourceStream.available() > 0);

            targetStream.write(sourceStream.readAllBytes());

            return targetFile.toPath().normalize();
        } catch (FileNotFoundException exception) {
            throw new RuntimeException(
                exception.getMessage().contains(sourceFile.getName())
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

    void validateFileSize(long fileSize) {
        if (fileSize > MAX_FILESIZE) {
            log.error("Filesize {} is exceeding max size {}", fileSize, MAX_FILESIZE);
            throw new IllegalArgumentException("Filesize too large");
        }
    }

    void ensureWriteable(File file) {
        var fileDirectory = file.getParentFile();

        if (fileDirectory.exists() && fileDirectory.isDirectory() && fileDirectory.canWrite())
            return;

        log.info("Creating missing target directory [{}] for [{}]", fileDirectory.getPath(), file.getPath());

        if (fileDirectory.exists() && fileDirectory.isDirectory())
            throw new IllegalArgumentException("File directory is not writeable");

        if (fileDirectory.exists() && !fileDirectory.isDirectory())
            throw new IllegalArgumentException("File directory is not a directory");

        if (!fileDirectory.exists() && fileDirectory.mkdirs())
            return;

        throw new IllegalArgumentException("Failed to create file directory");
    }

    public Path persistWebFile(User owner, FilePart file) {
        validateFileSize(file.size());

        var sourcePath = file instanceof FileUpload fileUpload ? fileUpload.uploadedFile() : file.filePath();
        var targetPath = uploadFolder.resolve(String.format("./%03d/web/%s", owner.getId(), UUID.randomUUID())).toAbsolutePath();

        return persistFile(sourcePath.toFile(), targetPath.toFile());
    }

    public Path persistFile(User owner, File file) {
        validateFileSize(file.length());

        var targetPath = uploadFolder.resolve(String.format("./%03d/file/%s", owner.getId(), UUID.randomUUID())).toAbsolutePath();

        return persistFile(file, targetPath.toFile());
    }

    public void delete(String path) {
        var realPath = uploadFolder.resolve(path);
        if (!realPath.toFile().exists())
            throw new IllegalArgumentException("Path must be defined and exist in upload folder.");

        delete(realPath);
    }

    public void delete(Path path) {
        if (path == null || !path.startsWith(uploadFolder))
            throw new IllegalArgumentException("Path must be defined and exist in upload folder.");

        if (!path.toFile().delete())
            throw new RuntimeException(String.format("Failed to delete file %s", path.getFileName()));
    }

    public File resolve(String path) {
        if (path == null)
            throw new IllegalArgumentException("Cannot resolve null-file.");

        var realPath = uploadFolder.resolve(path);
        if (!realPath.startsWith(uploadFolder))
            throw new IllegalArgumentException("Path must exist in upload folder.");

        var realFile = realPath.toFile();
        if (!realFile.exists() || !realFile.isFile())
            throw new IllegalArgumentException("Path must exist and be a file.");

        return realFile;
    }

    public File resolveSafe(String path) {
        try {
            return resolve(path);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getHash(Path path) {
        try {
            // 1. Get the MessageDigest instance for SHA-1
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            // 2. Read the file in chunks to handle large files efficiently
            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead;

            try (var fis = Files.newInputStream(path)) {
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            // 3. Convert the resulting byte array to a hex string
            return Hex.encodeHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            // This should never happen as SHA-1 is standard
            throw new RuntimeException("SHA-1 algorithm not available", e);
        } catch (IOException e) {
            return null;
        }
    }

    public String getHash(String path) {
        return getHash(uploadFolder.resolve(path));
    }

    public String getHash(File file) {
        return getHash(file.toPath());
    }
}
