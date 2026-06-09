package dev.roelofr.domains.chat.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatType {
    Regular("regular"),
    Group("group"),
    Issue("issue");

    private final String value;
}
