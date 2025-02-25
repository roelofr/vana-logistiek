package dev.roelofr.rest.dtos;

import dev.roelofr.domain.User;

public record EmbeddedUser(
    long id,
    String name
) {
    public EmbeddedUser(User user) {
        this(
            user.getId(),
            user.getName()
        );
    }
}
