package dev.roelofr;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class AppTest {
    @Inject
    LaunchMode launchMode;

    @Test
    void testApplicationBoots() {
        assertEquals(LaunchMode.TEST, launchMode);
    }
}
