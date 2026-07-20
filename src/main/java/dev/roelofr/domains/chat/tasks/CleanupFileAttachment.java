package dev.roelofr.domains.chat.tasks;

import dev.roelofr.AppUtil;
import dev.roelofr.domains.chat.ChatChannelService;
import dev.roelofr.domains.chat.model.ChatEntryRepository;
import dev.roelofr.domains.chat.model.ChatFile;
import dev.roelofr.domains.chat.model.ChatFileRepository;
import dev.roelofr.domains.chat.model.FileStatus;
import dev.roelofr.domains.chat.model.FileType;
import dev.roelofr.domains.chat.util.ImageUtil;
import dev.roelofr.events.ChatFileUploaded;
import dev.roelofr.service.FileService;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;


@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CleanupFileAttachment {
    final static List<String> WANTED_FILE_TYPES = List.of("webp", "avif", "jpeg");
    /**
     * Maximum dimension for resized images, in pixels.
     */
    final static int MAXIMUM_IMAGE_SIZE = 1200;

    private final ChatEntryRepository chatEntryRepository;
    private final ChatFileRepository chatFileRepository;
    private final ImageUtil imageUtil;
    private final LaunchMode launchMode;
    private final ChatChannelService chatChannelService;
    private final FileService fileService;

    void sleep(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            // noop
        }
    }

    @Startup
    void convertOnStartupOnDev() {
        if (!launchMode.isDev())
            return;

        log.info("DEV MODE: processing files.");

        convertUnprocessed();
    }

    @Transactional
    @Scheduled(every = "15m", delayed = "5m")
    void convertOnSchedule() {
        convertUnprocessed();
    }

    @Transactional
    void convertUnprocessed() {
        chatFileRepository.findUnprocessedFiles()
            .forEach(this::convertChatFile);
    }

    @Transactional
    void convertOnCreation(@ObservesAsync ChatFileUploaded event) {
        // Delay, unless testing
        if (!launchMode.equals(LaunchMode.TEST))
            sleep(Duration.ofMillis(300));

        var repositoryEntry = chatEntryRepository.findById(event.id());

        if (!(repositoryEntry instanceof ChatFile chatFile))
            return;

        if (chatFile.isFileReady())
            return;

        convertChatFile(chatFile);
    }

    @Transactional
    void convertChatFile(ChatFile chatFile) {
        log.info("Processing {}", chatFile.getId());

        try {
            File oldFile = fileService.resolve(chatFile.getPath());
            Path oldPath = oldFile.toPath();

            Path newPath = convertImageToSomethingPredictable(oldPath);

            log.info("File {} was updated from path {} to {}", chatFile.getId(), oldPath, newPath);

            try {
                chatFile.setPath(fileService.relativize(newPath));
            } catch (IllegalArgumentException e) {
                chatFile.setPath(newPath.toString());
            }

            if (chatFile.getFilename() != null) {
                chatFile.setFilename(String.format(
                    "%s.%s",
                    AppUtil.getFilenameWithoutExtension(chatFile.getFilename()),
                    AppUtil.getExtension(newPath.toString())
                ));
            } else {
                chatFile.setFilename(String.format(
                    "upload-%d.%s",
                    chatFile.getId(),
                    AppUtil.getExtension(newPath.toString())
                ));
            }

            chatFile.setFileStatus(FileStatus.Ready);
            chatFile.setFileType(FileType.Image);

            log.info("Converted chat file {}, {} → {}", chatFile.getId(), oldPath.getFileName(), newPath.getFileName());

            if (oldFile.isFile() && oldFile.delete())
                log.info("Deleted old file");
        } catch (IOException | RuntimeException e) {
            chatFile.setFileStatus(FileStatus.Corrupted);

            log.error("Failed to convert chat file {}, {}", chatFile.getId(), e.getMessage(), e);
        } finally {
            chatChannelService.sendChatEntry(chatFile);
        }
    }

    Path convertImageToSomethingPredictable(Path path) throws IOException {
        // TODO scan for bad shit

        // Load image
        var image = imageUtil.loadImage(path);

        // Resize to something safe
        image = imageUtil.containImageInSquare(image, MAXIMUM_IMAGE_SIZE);

        // TODO Remove metadata
        // NOTE Metadata isn't transferred by ImageIO

        // TODO cleanup file

        // Write file
        return writeImage(path, image);
    }


    /**
     * Rotates the provided image based on the EXIF orientation metadata in the given file.
     *
     * @param image the original {@code BufferedImage} that needs to be rotated.
     * @param file  the file path containing the image with EXIF metadata.
     * @return a rotated {@code BufferedImage} based on the EXIF orientation, or the original image if no rotation is needed.
     */
    BufferedImage rotateImageFromExif(BufferedImage image, Path file) {
        // Determine rotation
        var rotation = imageUtil.determineExifRotation(file);

        if (rotation == 1)
            return image;

        // Rotate using imageUtil
        return imageUtil.applyExifRotation(image, rotation);
    }

    /**
     * Writes the provided image to a new file with a unique name and the specified file type.
     *
     * @param sourceFile the original file path from which the image was extracted.
     * @param image      the {@code BufferedImage} to be written to a file.
     * @return the path of the newly created file containing the image.
     * @throws IOException if there is an error writing the image to the file.
     */
    Path writeImage(Path sourceFile, BufferedImage image) throws IOException {
        for (var type : WANTED_FILE_TYPES) {
            var newPath = sourceFile.resolveSibling(String.format("%s-converted.%s", UUID.randomUUID(), type));
            var writeOk = ImageIO.write(image, type, newPath.toFile());
            if (writeOk)
                return newPath;
        }

        throw new RuntimeException("Failed to convert file!");
    }

}
