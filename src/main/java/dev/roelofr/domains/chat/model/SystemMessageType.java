package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.util.StringUtil;

public enum SystemMessageType {
    Created,
    UserAdded,
    GroupAdded,
    Resolved,
    Unresolved,
    Closed,
    Reopened;

    @JsonValue
    public String value() {
        return StringUtil.hyphenate(name());
    }
}
