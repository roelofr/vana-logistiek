package dev.roelofr.domains.chat.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A member of a chat, either a group or a user.
 */
@Builder
public record ChatMemberDto(
    @NotNull MemberType type,
    @NotNull @Positive Long id
) {
    public boolean isGroup() {
        return type == MemberType.Group;
    }

    public boolean isUser() {
        return type == MemberType.User;
    }

    @Getter
    @RequiredArgsConstructor
    public enum MemberType {
        Group("group"),
        User("user");

        @JsonValue
        private final String name;
    }
}
