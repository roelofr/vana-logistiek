package dev.roelofr.domains.chat.dto;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatState;
import dev.roelofr.domains.chat.model.ChatType;

import java.time.LocalDateTime;
import java.util.List;

public record ChatDto(
    long id,
    String title,
    ChatType type,
    ChatState state,
    List<ChatParticipantDto> users,
    List<ChatParticipantDto> groups,
    ChatSubjectDto subject,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean unread
) {
    public ChatDto(Chat chat, boolean unread) {
        this(
            chat.getId(),
            chat.getTitle(),
            chat.getType(),
            chat.getState(),
            chat.getUsers().stream().map(ChatParticipantDto::new).toList(),
            chat.getGroups().stream().map(ChatParticipantDto::new).toList(),
            ChatSubjectDto.fromNullable(chat.getSubject()),
            chat.getCreatedAt(),
            chat.getUpdatedAt(),
            unread
        );
    }

    public ChatDto(Chat chat) {
        this(chat, false);
    }
}
