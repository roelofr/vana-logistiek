package dev.roelofr.domains.chat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * A request to add one more more participants to a chat
 *
 * @param members Members that should be added to the chat.
 */
@Builder
public record AddChatParticipantsRequests(
    @NotNull @NotEmpty List<@Valid ChatMemberDto> members
) {
    public List<Long> groups() {
        return members.stream()
            .filter(ChatMemberDto::isGroup)
            .map(ChatMemberDto::id)
            .toList();
    }

    public List<Long> users() {
        return members.stream()
            .filter(ChatMemberDto::isUser)
            .map(ChatMemberDto::id)
            .toList();
    }

}
