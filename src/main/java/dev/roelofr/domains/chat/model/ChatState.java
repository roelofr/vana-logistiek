package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatState {
    Active("active"),
    Permanent("permanent"),
    Closed("closed");

    @JsonValue
    private final String value;
}
