package dev.roelofr.domains.users.dto;

import jakarta.validation.constraints.Positive;

public record OnboardRequest(
    @Positive Long groupId
) {
}
