package dev.roelofr.domains.users.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.util.StringUtil;

public enum GroupFlags {
    /**
     * Group should always be added
     */
    AlwaysAdd,
    /**
     * Group is a system group.
     */
    System;

    @JsonValue
    public String value() {
        return StringUtil.hyphenate(name());
    }
}
