package dev.roelofr.rest.responses;

import java.util.Collection;

public record ProfileResponse(
    long id,
    String name,
    String email,
    Collection<String> roles,
    Collection<String> groups,
    String avatarUrl
) {
}
