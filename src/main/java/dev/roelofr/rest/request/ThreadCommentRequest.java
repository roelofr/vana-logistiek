package dev.roelofr.rest.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ThreadCommentRequest(
    @NotBlank @Length(max = 1024 * 1000) String message
) {
}
