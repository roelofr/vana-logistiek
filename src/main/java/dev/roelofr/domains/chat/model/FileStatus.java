package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.util.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileStatus {
    New,
    Ready,
    Corrupted;

    @JsonValue
    public String value() {
        return StringUtil.hyphenate(name());
    }
}
