package dev.roelofr.rest.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public record ErrorDto(
    @JsonInclude(ALWAYS)
    int code,

    @JsonInclude(ALWAYS)
    String message,

    @JsonInclude(NON_NULL)
    String field
) {
    public static ErrorDto forField(String field, String message) {
        return new ErrorDto(-1, message, field);
    }

    public ErrorDto(int code, String message) {
        this(code, message, null);
    }
}
