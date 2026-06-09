package dev.roelofr.domains.chat.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
    Image("image"),
    Binary("binary"),
    Unknown("unknown");

    private final String value;
}
