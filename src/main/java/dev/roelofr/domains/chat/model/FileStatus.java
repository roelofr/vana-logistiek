package dev.roelofr.domains.chat.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileStatus {
    New("new"),
    Ready("ready"),
    Corrupted("corrupted");

    private final String value;
}
