package dev.roelofr.integrations.hanko.model;

import jakarta.validation.constraints.NotNull;

public record HankoEmailAddress(
    @NotNull String address,
    boolean isPrimary,
    boolean isVerified
) {
    @Override
    public String toString() {
        return address;
    }
}
