package dev.roelofr.integrations.hanko.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record SessionValidationRequest(
    @JsonProperty("session_token")
    @NotBlank String sessionToken
) {
}
