package dev.roelofr.rest.request;

import dev.roelofr.Constants;

import java.util.List;
import java.util.Optional;

public record CreateUserRequest(
    String name,
    String email,
    List<String> roles,
    Optional<Long> district
) {
    public String cleanEmail() {
        return this.email().toLowerCase(Constants.Dutch).trim();
    }
}
