package dev.roelofr.domains.users;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Views {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Private extends Public {
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Admin extends Public {
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Public {
    }
}
