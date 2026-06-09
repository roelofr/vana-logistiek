package dev.roelofr.domains.chat.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * A request to create a chat.
 *
 * @param title   User-provided title of the chat
 * @param members Members that should be part of the chat, requesting user need not be part of this group.
 */
@Builder
public record CreateChatRequest(
    @NotNull @Length(min = 2, max = 200) String title,
    @NotNull @NotEmpty List<@Valid ChatMember> members
) {
    public List<Long> groups() {
        return members.stream()
            .filter(group -> group.type() == MemberType.Group)
            .map(ChatMember::id)
            .toList();
    }

    public List<Long> users() {
        return members.stream()
            .filter(group -> group.type() == MemberType.User)
            .map(ChatMember::id)
            .toList();
    }

    /**
     * A member of a chat, either a group or a user.
     */
    @Builder
    public record ChatMember(
        @NotNull MemberType type,
        @NotNull @Positive Long id
    ) {
        //
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
