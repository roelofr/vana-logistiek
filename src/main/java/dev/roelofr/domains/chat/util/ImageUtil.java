package dev.roelofr.domains.chat.util;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ApplicationScoped
public class ImageUtil {
    private static final String TREE_IMAGEIO_NATIVE = "javax_imageio_1.0";
    private static final String TREE_IMAGEIO_JPEG = "javax_imageio_jpeg_image_1.0";

    private static final List<String> SUPPORTED_MIMES = List.of(
        "image/heic", "image/hevc",
        "image/jpeg", "image/png", "image/webp", "image/avif"
    );

    final Tika tika = new Tika();

    /**
     * Loads an image from the specified file path and returns it as a {@code BufferedImage}.
     * The method supports multiple image formats, including JPEG, PNG, WEBP, HEIC, and AVIF.
     * Custom loaders are invoked for HEIC and AVIF formats, while standard formats
     * are handled using {@code ImageIO}.
     *
     * @param path the {@code Path} to the image file to be loaded.
     * @return a {@code BufferedImage} representing the loaded image.
     * @throws IOException      if an I/O error occurs while reading the file.
     * @throws RuntimeException if the file does not exist or its MIME type is unsupported.
     */
    public BufferedImage loadImage(Path path) throws IOException {
        final String filename = path.getFileName().toString();
        final var file = path.toFile();

        log.info("Resolved file {}", filename);

        return withImageReader(file, reader -> {
            return reader.read(0); // Read the
        });
    }

    /**
     * Determines the EXIF rotation of an image by reading its metadata.
     * The method attempts to extract the orientation tag from the EXIF data of the image file.
     * If the orientation information is not available or an error occurs, a default orientation of 1 is returned.
     *
     * @param path the path to the image file, provided as a {@code Path} object.
     * @return the EXIF orientation value as an integer, where:
     * 1 = Normal,
     * 3 = Upside Down,
     * 6 = Rotated 90 degrees right,
     * 8 = Rotated 90 degrees left.
     */
    public int determineExifRotation(Path path) {
        // 0. Path to file
        var file = new File(path.toUri());

        // 1. Act on a reader
        try {
            return withImageReader(file, reader -> {
                // 3. Get the metadata
                IIOMetadata metadata = reader.getImageMetadata(0);

                if (List.of(metadata.getMetadataFormatNames()).contains(TREE_IMAGEIO_NATIVE)) {
                    var imageIoFormatMeta = (IIOMetadataNode) metadata.getAsTree(TREE_IMAGEIO_NATIVE);
                    return walkExifTreeForOrientation(TREE_IMAGEIO_NATIVE, imageIoFormatMeta);
                }

                var defaultFormat = metadata.getNativeMetadataFormatName();
                var defaultFormatMeta = (IIOMetadataNode) metadata.getAsTree(defaultFormat);
                return walkExifTreeForOrientation(defaultFormat, defaultFormatMeta);
            });
        } catch (IOException e) {
            log.warn("Failed to read EXIF orientation from image {}: {}", file.getName(), e.getMessage());
            return 1;
        }
    }

    /**
     * Rotates the image based on EXIF orientation.
     * Supports only: 1 (Normal), 3 (180), 6 (90 CW), 8 (270 CW).
     * Throws IllegalArgumentException for unsupported orientations (2, 4, 5, 7).
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public BufferedImage applyExifRotation(BufferedImage src, int orientation) {
        int width = src.getWidth();
        int height = src.getHeight();

        // Case 1: Normal - Return original (or a copy if you need to detach references)
        if (orientation == 1) {
            return src;
        }

        AffineTransform transform = new AffineTransform();
        int destWidth = width;
        int destHeight = height;

        switch (orientation) {
            case 3: // Rotate 180 degrees
                // Translate to bottom-right, then rotate PI (180)
                transform.translate(width, height);
                transform.rotate(Math.PI);
                break;

            case 6: // Rotate 90 degrees Clockwise
                // Rotate 90 deg, then translate down by width to keep it in positive coordinates
                transform.rotate(Math.toRadians(90));
                transform.translate(0, -width);

                // Swap width and height
                destWidth = height;
                destHeight = width;
                break;

            case 8: // Rotate 270 degrees Clockwise (or 90 Counter-Clockwise)
                // Rotate -90 deg, then translate left by height
                transform.rotate(Math.toRadians(-90));
                transform.translate(-height, 0);

                // Swap width and height
                destWidth = height;
                destHeight = width;
                break;

            default:
                // Throw exception for non-rotational cases (2, 4, 5, 7)
                throw new IllegalArgumentException(
                    String.format(
                        "Rotation-only mode does not support orientation: %d. Use full logic for flips (2, 4, 5, 7).",
                        orientation
                    )
                );
        }

        // Create the destination image
        // TYPE_INT_ARGB is safe for transparency, use TYPE_INT_RGB if you know it's opaque
        BufferedImage dest = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dest.createGraphics();
        try {
            // Enable high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the transformed image
            g2d.drawImage(src, transform, null);
        } finally {
            g2d.dispose();
        }

        return dest;
    }

    /**
     * Resizes the provided image to fit within a square with the specified maximum side length, maintaining its aspect ratio.
     * If the image already fits within the specified dimensions, it is returned unchanged.
     *
     * @param image     the original {@code BufferedImage} that needs to be resized.
     * @param maxLength the maximum allowed width or height for the resized image.
     * @return a resized {@code BufferedImage} with dimensions constrained by {@code maxLength}, or the original image if no resizing is necessary.
     */
    public BufferedImage containImageInSquare(BufferedImage image, int maxLength) {
        var resizeTo = scaledDimensions(image, maxLength);

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

    /**
     * Retrieves the orientation of an image from its metadata.
     *
     * @param root the root node of the image metadata.
     * @return the orientation value, or 1 if not found.
     * @throws IOException if an I/O error occurs while reading the metadata.
     */
    private int walkExifTreeForOrientation(String treeName, IIOMetadataNode root) throws IOException {
        if (TREE_IMAGEIO_NATIVE.equals(treeName)) {
            var rotation = findNode(root, List.of("Dimension", "ImageOrientation"), "value");
            if (rotation instanceof String rotationString) {
                return switch (rotationString) {
                    case "Rotate180" -> 3;
                    case "Rotate90" -> 8;
                    case "Rotate270" -> 6;
                    default -> 1;
                };
            }
        } else if (TREE_IMAGEIO_JPEG.equals(treeName)) {
            var exifOrientation = findNode(root, List.of("ExifIFD", "TIFFTag"), "Orientation");
            if (exifOrientation instanceof String exifString && !exifString.isEmpty()) {
                return Integer.parseInt(exifString, 10);
            }
        }
        return 1;
    }

    private Object findNode(IIOMetadataNode root, List<String> path, String tagName) {
        var treeIterator = new AtomicInteger(0);
        var pathIterator = new AtomicInteger(0);

        var targetTree = root.getChildNodes();
        var targetPath = path.get(pathIterator.getAndIncrement());

        do {
            var currentNode = targetTree.item(treeIterator.getAndIncrement());
            if (!currentNode.getNodeName().equals(targetPath))
                continue;

            if (pathIterator.get() < path.size()) {
                treeIterator.set(0);
                targetTree = currentNode.getChildNodes();
                targetPath = path.get(pathIterator.getAndIncrement());
                continue;
            }

            if (tagName == null)
                return currentNode;

            var attribute = ((IIOMetadataNode) currentNode).getAttribute(tagName);
            return attribute.isEmpty() ? null : attribute;
        } while (treeIterator.get() < targetTree.getLength());

        return null;
    }

    private <T> T withImageReader(File file, ActsWithImageReader<T> invoke) throws IOException {
        // 0. Fail if not found
        if (!file.exists())
            throw new RuntimeException("File %s not found!".formatted(file.getName()));

        String fileMime = tika.detect(file);


        // 1. Create an ImageInputStream from the File
        // This allows ImageIO to peek at the bytes to detect the format
        try (ImageInputStream input = ImageIO.createImageInputStream(file)) {
            // 2. Get all registered readers that claim they can read this stream
            // This method inspects the "magic numbers" at the start of the file
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext())
                throw new IOException(String.format(
                    "No suitable ImageReader found for %s file: %s. Unsupported format or corrupted file.",
                    file.getName(), fileMime
                ));

            // 3. Use the first ImageReader we can find
            ImageReader reader = readers.next();

            var readerClass = reader.getClass().getName();
            if (!readerClass.startsWith("com.twelvemonkeys.imageio"))
                log.warn("Using fallback ImageReader {} for {}", readerClass, file.getName());

            try {
                // 4. Set the input and forward the call
                reader.setInput(input);
                return invoke.apply(reader);
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * Calculates the dimensions to scale an image to fit within a maximum size while maintaining aspect ratio.
     *
     * @param image         the original {@code BufferedImage} for which dimensions are calculated.
     * @param maximumLength the maximum allowed width and height for the scaled image.
     * @return a {@code Dimension} object with the scaled width and height, or {@code null} if no scaling is needed.
     */
    @Nullable
    private Dimension scaledDimensions(BufferedImage image, int maximumLength) {
        var imageWidth = (double) image.getWidth();
        var imageHeight = (double) image.getHeight();
        var wantedLength = (double) maximumLength;

        log.info("Fitting {}x{} into {}x{} ", imageWidth, imageHeight, maximumLength, maximumLength);

        if (imageWidth <= wantedLength && imageHeight <= wantedLength)
            return null;

        if (imageWidth > imageHeight)
            return new Dimension((int) wantedLength, (int) (imageHeight / imageWidth * wantedLength));
        else
            return new Dimension((int) (imageWidth / imageHeight * wantedLength), (int) wantedLength);
    }

    private interface ActsWithImageReader<T> {
        T apply(ImageReader reader) throws IOException;
    }
}
