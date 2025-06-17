package dev.roelofr;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {
    public static String mobileName(String name) {
        var cleanName = name.toLowerCase().trim();

        if (cleanName.length() <= 3)
            return cleanName;

        var noVowels = cleanName.replaceAll("[aeoiu]", "");
        if (noVowels.length() >= 3)
            return noVowels.substring(0, 3);

        return cleanName.substring(0, 3);
    }
}
