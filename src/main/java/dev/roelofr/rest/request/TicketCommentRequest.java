package dev.roelofr.rest.request;

import jakarta.validation.constraints.NotBlank;

public record TicketCommentRequest(@NotBlank String comment) {
}
