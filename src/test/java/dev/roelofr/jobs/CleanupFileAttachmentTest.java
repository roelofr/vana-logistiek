package dev.roelofr.jobs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CleanupFileAttachmentTest {
    private static final Dimension WANTED_DIMENSIONS = new Dimension(400, 300);

    @InjectMocks
    CleanupFileAttachment cleanupFileAttachment;

    @ParameterizedTest
    @MethodSource("provideDimensions")
    void scaledDimensionsTooTall(int imageWidth, int imageHeight, int expectedWidth, int expectedHeight) {
        var image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        var result = cleanupFileAttachment.scaledDimensions(image, WANTED_DIMENSIONS);

        assertEquals(expectedWidth, result.getWidth(), String.format("Expected image width to equal %d, got %.0f",  expectedWidth, result.getWidth()));
        assertEquals(expectedHeight, result.getHeight(), String.format("Expected image height to equal %d, got %.0f",  expectedHeight, result.getHeight()));
    }

    private static Stream<Arguments> provideDimensions() {
        return Stream.of(
            Arguments.of(200, 150, 200, 150),
            Arguments.of(400, 300, 400, 300),
            Arguments.of(600, 600, 300, 300),
            Arguments.of(4000, 3000, 400, 300),
            Arguments.of(1920, 1080, 400, 225),
            Arguments.of(800, 200, 400, 100),
            Arguments.of(200, 400, 150, 300)
        );
    }

}
