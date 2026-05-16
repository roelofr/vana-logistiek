package dev.roelofr.domains.chat.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageUtilTest {
    final String TEST_IMAGE_PATH = "src/test/resources/test-images";
    final Map<String, Path> resourceFile = new HashMap<>();

    @BeforeAll
    void createTempFilesFromResources(@TempDir Path tempDir) throws IOException {
        var testFile = new File(TEST_IMAGE_PATH);
        if (!testFile.exists() || !testFile.isDirectory()) throw new IOException("Test image directory does not exist");

        for (var file : Objects.requireNonNull(testFile.listFiles())) {
            var fileName = file.getName();
            var fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
            if (fileExtension.length() != 4) continue;

            var tempFile = tempDir.resolve(file.getName());
            Files.copy(file.toPath(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            resourceFile.put(fileName, tempFile);
        }
    }

    List<Arguments.ArgumentSet> provideFiles() {
        return resourceFile.entrySet().stream().map(entry -> Arguments.argumentSet(entry.getKey(), entry.getValue())).toList();
    }

    final ImageUtil imageUtil = new ImageUtil();

    @ParameterizedTest
    @MethodSource("provideFiles")
    void loadImage(Path path) {
        try {
            var result = imageUtil.loadImage(path);
            assertInstanceOf(BufferedImage.class, result);

        } catch (IOException e) {
            if (e.getMessage().contains("No suitable ImageReader") && e.getMessage().contains("image/avif"))
                abort("Skipping AVIF test, as its support is OS-dependent.");

            fail(e);
        }

    }

    @Disabled("Exif data seems flakey, lets do this client-side for now")
    @ParameterizedTest
    @MethodSource("provideFiles")
    void determineExifRotationDoesNotErrorOnSupportedItems(Path path) {
        var result = assertDoesNotThrow(() -> imageUtil.determineExifRotation(path));

        assertTrue(result > 0, String.format("Expected %d to be greater than 0", result));
    }

    @Disabled("Exif data seems flakey, lets do this client-side for now")
    @ParameterizedTest(name = "Exif data for {1}")
    @CsvSource(textBlock = """
        amanda-swanepoel.jpeg,1
        amanda-swanepoel-exif-rotate-3.jpeg,3
        amanda-swanepoel-exif-rotate-6.jpeg,6
        amanda-swanepoel-exif-rotate-3.webp,3
        amanda-swanepoel-exif-rotate-6.webp,6
        amanda-swanepoel-exif-rotate-6.heif,6
        """)
    void determineExifRotation(String resource, int rotation) {
        assumeTrue(resourceFile.containsKey(resource), String.format("Resource file '%s' not found", resource));
        var path = resourceFile.get(resource);

        assumeImageLoads(path, resource);

        var result = assertDoesNotThrow(() -> imageUtil.determineExifRotation(path));
        assertEquals(rotation, result, "Exif rotation does not match expected value");
    }

    @Disabled("Exif data seems flakey, lets do this client-side for now")
    @ParameterizedTest(name = "Rotate {1} with EXIF {2}")
    @CsvSource(textBlock = """
        amanda-swanepoel-small.jpeg,1,400,600
        amanda-swanepoel-small.jpeg,3,400,600
        amanda-swanepoel-small.jpeg,6,600,400
        amanda-swanepoel-small.jpeg,8,600,400
        """)
    void applyExifRotation(String resource, int rotation, int expectedWidth, int expectedHeight) {
        assumeTrue(resourceFile.containsKey(resource), String.format("Resource file '%s' not found", resource));
        var image = assumeImageLoads(resourceFile.get(resource), resource);

        var result = assertDoesNotThrow(() -> imageUtil.applyExifRotation(image, rotation));

        assertInstanceOf(BufferedImage.class, result);
        assertEquals(expectedWidth, result.getWidth(), "Image width does not match expected value");
        assertEquals(expectedHeight, result.getHeight(), "Image height does not match expected value");
    }

    @ParameterizedTest(name = "Scale {0} into {2}")
    @CsvSource(textBlock = """
        amanda-swanepoel.jpeg,900,600,900
        amanda-swanepoel.heif,900,600,900
        amanda-swanepoel.avif,900,600,900
        amanda-swanepoel.webp,900,600,900
        amanda-swanepoel-small.jpeg,800,400,600
        amanda-swanepoel-small.jpeg,90,60,90
        amanda-swanepoel-small.webp,800,400,600
        amanda-swanepoel-small.webp,90,60,90
        """)
    void containImageInSquareResourceTest(String resource, int maxSize, int expectedWidth, int expectedHeight) {
        assumeTrue(resourceFile.containsKey(resource), String.format("Resource file '%s' not found", resource));
        var image = assumeImageLoads(resourceFile.get(resource), resource);

        var result = assertDoesNotThrow(() -> imageUtil.containImageInSquare(image, maxSize));

        assertInstanceOf(BufferedImage.class, result);

        log.info("Resized image to {}x{} pixels", result.getWidth(), result.getHeight());

        assertEquals(expectedWidth, result.getWidth(), "Image width does not match expected value");
        assertEquals(expectedHeight, result.getHeight(), "Image height does not match expected value");
    }

    @ParameterizedTest(name = "Fit {1}x{2} into {0}")
    @CsvSource(textBlock = """
        400, 200, 150, 0, 0
        400, 400, 300, 0, 0
        400, 600, 600, 400, 400
        400, 4000, 3000, 400, 300
        400, 1920, 1080, 400, 225
        400, 800, 200, 400, 100
        300, 200, 400, 150, 300
        """)
    void containImageInSquareScalingTest(int maxLength, int imageWidth, int imageHeight, Integer expectedWidth, Integer expectedHeight) {
        var image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        assumeTrue(image.getWidth() == imageWidth, "Failed creating image of expected width");
        assumeTrue(image.getHeight() == imageHeight, "Failed creating image of expected height");

        var result = imageUtil.containImageInSquare(image, maxLength);

        if (expectedWidth == 0 && expectedHeight == 0) {
            assertSame(image, result, "Failed asserting image was returned as-is");
            assertEquals(imageWidth, result.getWidth(), "Failed asserting image width was not modified");
            return;
        }

        assumeTrue(expectedWidth != null, "Width and height need to both be null, or neither.");
        assumeTrue(expectedHeight != null, "Width and height need to both be null, or neither.");

        assertNotNull(result);
        assertEquals((int) expectedWidth, result.getWidth(), String.format("Expected image width to equal %d, got %d", expectedWidth, result.getWidth()));
        assertEquals((int) expectedHeight, result.getHeight(), String.format("Expected image height to equal %d, got %d", expectedHeight, result.getHeight()));
    }

    BufferedImage assumeImageLoads(Path path, String name) {
        try {
            return imageUtil.loadImage(path);
        } catch (IOException t) {
            abort(MessageFormat.format("Failed to load file for {0}: {1}", name, t.getMessage()));
            throw new RuntimeException("Test failed, but signature of abort is invalid.");
        }
    }

}
