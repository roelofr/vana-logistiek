package dev.roelofr;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppUtilTest {
    @ParameterizedTest
    @CsvSource(value = {
        "100,100_000",
        "201a,201_001",
        "340ab,340_028",
        "A30,1_0030_000",
        "B40c,2_0040_003"
    }, delimiter = ',')
    void parseVendorNumberToInteger(String input, String outputAsString) {
        int expected = Integer.parseInt(outputAsString.replaceAll("_", ""), 10);

        int result = AppUtil.parseVendorNumberToInteger(input);

        assertEquals(expected, result, () -> String.format("Expected %s to map to number %d, got %s instead", input, expected, result));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "a,1",
        "z,26",
        "aa,27",
        "ab,28",
        "zz,702",
        "aaa,703",
        "bcd,1434"
    })
    void stringToAlphabetPosition(String input, String outputAsString) {
        int expected = Integer.parseInt(outputAsString, 10);

        int result = AppUtil.stringToAlphabetPosition(input);

        assertEquals(expected, result, () -> String.format("Expected %s to map to number %d, got %s instead", input, expected, result));
    }

    @ParameterizedTest
    @CsvSource(nullValues = {"NULL"}, value = {
        "Hello World.png, png",
        "Oh-My-Zsh.sh.example, example",
        "oh-no, NULL"
    })
    void getExtension(String input, String expectedOutput) {
        var result = AppUtil.getExtension(input);

        assertEquals(expectedOutput, result);
    }

    @ParameterizedTest
    @CsvSource({
        "Hello World.png, Hello World",
        "Oh-My-Zsh.sh.example, Oh-My-Zsh.sh",
        "oh-no, oh-no",
    })
    void getFilenameWithoutExtension(String input, String expectedOutput) {
        var result = AppUtil.getFilenameWithoutExtension(input);

        assertEquals(expectedOutput, result);
    }

    @ParameterizedTest
    @CsvSource({
        "Hello World.jpg, Hello World.jpg",
        ".htaccess, .htaccess",
        "inv!lid\\file\tname.bz2, invlidfilename.bz2",
        "IMG_20251029_094824(1).jpg, IMG_20251029_0948241.jpg",
        "1234567890123456789012345678901234567890.tar.gz,12345678901234567890123456789.gz"
    })
    void cleanupFilename(String filename, String expectedResult) {
        var result = AppUtil.cleanupFilename(filename, 32);

        assertEquals(expectedResult, result);
    }
}
