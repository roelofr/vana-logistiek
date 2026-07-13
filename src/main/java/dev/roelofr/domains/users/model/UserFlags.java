package dev.roelofr.domains.users.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.util.StringUtil;

public enum UserFlags {
    Onboarded;

    @JsonValue
    public String value() {
        return StringUtil.hyphenate(name());
    }
}
