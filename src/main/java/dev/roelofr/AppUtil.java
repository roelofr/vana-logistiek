package dev.roelofr;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static dev.roelofr.Constants.LocaleDutch;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppUtil {
    private static Pattern USABLE_NUMBER_MATCH = Pattern.compile("^([a-z])?([1-9][0-9]{0,3})([a-z]+)?$");
    private static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static Integer parseVendorNumberToInteger(String number) {
        if (number == null)
            return null;

        var cleanNumber = number.toLowerCase(LocaleDutch).trim();

        log.info("Parsed {} to clean number {}", number, cleanNumber);

        var usableMatcher = USABLE_NUMBER_MATCH.matcher(cleanNumber);
        if (usableMatcher.matches()) {
            var prefix = usableMatcher.group(1);
            var actualNumber = Integer.parseInt(usableMatcher.group(2), 10);
            var suffix = usableMatcher.group(3);

            log.info("Parsed number to {} / {} / {}", prefix, actualNumber, suffix);

            return Integer.parseInt(String.format("%d%04d%03d", stringToAlphabetPosition(prefix), actualNumber, stringToAlphabetPosition(suffix)));
        }

        log.info("Failed to parse [{}] as regular number", cleanNumber);

        var justTheNumbers = cleanNumber.replaceAll("[^0-9]+", "");
        if (justTheNumbers.isBlank()) {
            log.warn("Failed to parse number {} to something usable", number);
            return null;
        }

        log.info("Parsed {} into just-number {}", number, justTheNumbers);

        return Integer.parseInt(justTheNumbers, 10);
    }

    /**
     * Word to resolve to alphabet. Should already be cleaned.
     *
     * @param word
     * @return
     */
    static int stringToAlphabetPosition(String word) {
        if (word == null || word.isBlank())
            return 0;

        final var endValue = new AtomicInteger(0);
        for (int character = 0; character < word.length(); character++) {
            var letter = word.charAt(character);
            var alphabetWeight = ALPHABET.indexOf(letter) + 1;

            endValue.getAndUpdate(value -> value * 26 + alphabetWeight);
        }

        return endValue.get();
    }
}
