package dev.roelofr.jobs;

import dev.roelofr.Events;
import dev.roelofr.domain.ThreadUpdate;
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


@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CleanupFileAttachment {
    final static Dimension WANTED_DIMENSION = new Dimension(1200, 1200);
    final EntityManager em;

    final Tika tika = new Tika();

    @Blocking
    @Transactional
    @Incoming(Events.ThreadUpdateCreated)
    void convertThreadAttachment(ThreadUpdate update) {
        if (!(update instanceof ThreadUpdate.ThreadAttachment attachment))
            return;

        em.refresh(attachment);

        if (attachment.isFileReady())
            return;

        try {
            Path oldPath = attachment.getFilePath();

            Path newPath = convertImageToAnonymousImage(oldPath);

            attachment.setFilePath(newPath);

            attachment.setFileReady(true);

            log.info("Converted attachment {}, {} → {}", attachment.getId(), oldPath.getFileName(), newPath.getFileName());
        } catch (RuntimeException e) {
            log.error("Failed to convert attachment {}, {}", attachment.getId(), e.getMessage());
        }
    }

    Path convertImageToAnonymousImage(Path path) {
        // Load image
        var image = loadImage(path);

        // TODO Remove metadata

        // Resize to something safe
        image = resizeImage(image, WANTED_DIMENSION);

        // TODO cleanup file

        // TODO export as webp

        // TODO write back file

        return path;
    }

    BufferedImage loadImage(Path path) {
        final String filename = path.getFileName().toString();
        final var file = path.toFile();

        log.info("Resolved file {}", filename);

        if (!file.exists())
            throw new RuntimeException("File %s not found!".formatted(filename));

        try {
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
        } catch (IOException exception) {
            log.warn("Failed to process image {}: {}", filename, exception.getMessage());
            throw new RuntimeException("Failed to process image: %s".formatted(exception.getMessage()), exception);
        }

    }

    BufferedImage loadHeicFile(Path path) {
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

    BufferedImage loadAvifFile(Path path) {
        // TODO
        throw new RuntimeException("NO idea how to support AVIF");
    }

    BufferedImage resizeImage(BufferedImage image, Dimension maximumSize) {
        var resizeTo = scaledDimensions(image, maximumSize);

        return image;
    }

    @Nullable
    Dimension scaledDimensions(BufferedImage image, Dimension maximumSize) {
        var imageWidth = (double) image.getWidth();
        var imageHeight = (double) image.getHeight();

        var wantedWidth = maximumSize.getWidth();
        var wantedHeight = maximumSize.getHeight();

        if (imageWidth < wantedWidth && imageHeight < wantedHeight)
            return null;

        var currentRatio = imageWidth / imageHeight;
        var wantedRatio = wantedWidth / wantedHeight;

        if (currentRatio > wantedRatio)
            return new Dimension((int) wantedWidth, (int) (imageHeight / imageWidth * wantedWidth));
        else
            return new Dimension((int) (imageWidth / imageHeight * wantedHeight), (int) wantedHeight);
    }
}
