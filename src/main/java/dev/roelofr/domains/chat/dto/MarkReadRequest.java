package dev.roelofr.domains.chat.dto;

import jakarta.validation.constraints.Positive;

public record MarkReadRequest(
    @Positive long entryId
) {
}
