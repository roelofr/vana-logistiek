package dev.roelofr.rest.dtos;

import dev.roelofr.domain.User;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record EmbeddedUser(
    long id,
    String name
) {
    public EmbeddedUser(@Nonnull User user) {
        this(
            user.getId(),
            user.getName()
        );
    }

    public static EmbeddedUser fromNullable(@Nullable User user) {
        if (user == null)
            return null;

        return new EmbeddedUser(user);
    }
}
