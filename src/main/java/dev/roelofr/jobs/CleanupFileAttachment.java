package dev.roelofr.jobs;

import dev.roelofr.AppUtil;
import dev.roelofr.Events;
import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.enums.FileStatus;
import dev.roelofr.repository.ThreadUpdateRepository;
import dev.roelofr.service.ThreadService;
import io.quarkus.panache.common.Parameters;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openize.heic.decoder.HeicImage;
import openize.heic.decoder.PixelFormat;
import openize.io.IOFileStream;
import openize.io.IOMode;
import org.apache.tika.Tika;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CleanupFileAttachment {
    final static Dimension WANTED_DIMENSION = new Dimension(1200, 1200);
    final EntityManager em;

    final Tika tika = new Tika();
    private final ThreadService threadService;
    private final ThreadUpdateRepository threadUpdateRepository;

    @Startup
    void convertOnStartupOnDev() {
        if (! LaunchMode.current().isDev())
            return;

        log.info("Processing attachments created 5+ minutes ago, but not yet converted.");

        convertOnSchedule();
    }

    @Scheduled(every = "15m", delayed = "5m")
    void convertOnSchedule() {
        threadUpdateRepository.stream(
                """
                    SELECT tu
                    FROM ThreadUpdate tu
                    WHERE createdAt < :date
                        AND fileStatus = :status
                    """,
                Parameters.with("status", FileStatus.New)
                    .and("date", LocalDateTime.now().minus(Duration.ofMinutes(5)))
            )
            .forEach(threadUpdate -> {
                log.info("Re-processing {}", threadUpdate.getId());
                convertThreadAttachment(threadUpdate);
            });
    }

    @Blocking
    @Transactional
    @Incoming(Events.ThreadUpdateCreated)
    void convertThreadAttachment(ThreadUpdate update) {
        var syncedUpdate = threadService.findAttachmentById(update.getId());

        if (!(syncedUpdate instanceof ThreadUpdate.ThreadAttachment attachment))
            return;

        if (attachment.isFileReady())
            return;

        try {
            Path oldPath = attachment.getFilePath();

            Path newPath = convertImageToSomethingPredictable(oldPath);

            log.info("File was updated from path {} to {}", oldPath.toString(), newPath.toString());

            attachment.setFilePath(newPath);

            if (attachment.getFilename() != null) {
                attachment.setFilename(String.format(
                    "%s.%s",
                    AppUtil.getFilenameWithoutExtension(attachment.getFilename()),
                    AppUtil.getExtension(newPath.toString())
                ));
            } else {
                attachment.setFilename(String.format(
                    "upload-%d.%s",
                    attachment.getId(),
                    AppUtil.getExtension(newPath.toString())
                ));
            }

            attachment.setFileStatus(FileStatus.Ready);

            log.info("Converted attachment {}, {} → {}", attachment.getId(), oldPath.getFileName(), newPath.getFileName());
        } catch (IOException | RuntimeException e) {
            attachment.setFileStatus(FileStatus.Corrupted);

            log.error("Failed to convert attachment {}, {}", attachment.getId(), e.getMessage());
        }
    }

    Path convertImageToSomethingPredictable(Path path) throws IOException {
        // TODO scan for bad shit

        // Load image
        var image = loadImage(path);

        // Resize to something safe
        image = resizeImage(image, WANTED_DIMENSION);

        // TODO rotate according to metadata

        // TODO Remove metadata
        // NOTE Metadata isn't transferred by ImageIO

        // TODO cleanup file

        // Write file
        var newPath = path.resolveSibling(String.format("%s-converted.webp", UUID.randomUUID()));
        ImageIO.write(image, "webp", newPath.toFile());

        // Done :)
        return newPath;
    }

    BufferedImage loadImage(Path path) throws IOException {
        final String filename = path.getFileName().toString();
        final var file = path.toFile();

        log.info("Resolved file {}", filename);

        if (!file.exists())
            throw new RuntimeException("File %s not found!".formatted(filename));

        String mime = tika.detect(file);

        log.info("Detected mime type {}", mime);

        return switch (mime) {
            // Custom behaviour
            case "image/heic", "image/hevc" -> loadHeicFile(path);
            case "image/avif" -> loadAvifFile(path);
            // Default behaviour
            case "image/jpeg", "image/png", "image/webp" -> ImageIO.read(file);
            default ->
                throw new RuntimeException("File %s has mime %s, which cannot be parsed".formatted(filename, mime));
        };
    }

    BufferedImage loadHeicFile(Path path) throws IOException {
        try (var fs = new IOFileStream(path, IOMode.READ)) {
            var heicImage = HeicImage.load(fs);
            final var width = (int) heicImage.getWidth();
            final var height = (int) heicImage.getHeight();
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            final var dataBuffer = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
            final var defaultFramePixels = heicImage.getDefaultFrame().getInt32Array(PixelFormat.Argb32, dataBuffer);

            if (defaultFramePixels != dataBuffer)
                bufferedImage.setRGB(0, 0, width, height, defaultFramePixels, 0, width);

            return bufferedImage;
        }
    }

    BufferedImage loadAvifFile(Path path) throws IOException {
        // TODO
        throw new RuntimeException("NO idea how to support AVIF");
    }

    BufferedImage resizeImage(BufferedImage image, Dimension maximumSize) {
        var resizeTo = scaledDimensions(image, maximumSize);

        if (resizeTo == null)
            return image;

        var resizedImage = new BufferedImage(resizeTo.width, resizeTo.height, image.getType());

        var g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(image, 0, 0, resizeTo.width, resizeTo.height, null);
        g2d.dispose();

        return resizedImage;
    }

    @Nullable
    Dimension scaledDimensions(BufferedImage image, Dimension maximumSize) {
        var imageWidth = (double) image.getWidth();
        var imageHeight = (double) image.getHeight();

        var wantedWidth = maximumSize.getWidth();
        var wantedHeight = maximumSize.getHeight();

        if (imageWidth <= wantedWidth && imageHeight <= wantedHeight)
            return null;

        var currentRatio = imageWidth / imageHeight;
        var wantedRatio = wantedWidth / wantedHeight;

        if (currentRatio > wantedRatio)
            return new Dimension((int) wantedWidth, (int) (imageHeight / imageWidth * wantedWidth));
        else
            return new Dimension((int) (imageWidth / imageHeight * wantedHeight), (int) wantedHeight);
    }
}
