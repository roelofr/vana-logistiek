package dev.roelofr.domains.chat.dto;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatState;
import dev.roelofr.domains.chat.model.ChatType;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;

import java.time.LocalDateTime;
import java.util.List;

public record ChatDto(
    long id,
    String title,
    ChatType type,
    ChatState state,
    List<ChatUserDto> users,
    List<ChatGroupDto> groups,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public ChatDto(Chat chat) {
        this(
            chat.getId(),
            chat.getTitle(),
            chat.getType(),
            chat.getState(),
            chat.getUsers().stream().map(ChatUserDto::new).toList(),
            chat.getGroups().stream().map(ChatGroupDto::new).toList(),
            chat.getCreatedAt(),
            chat.getUpdatedAt()
        );
    }

    public record ChatUserDto(
        long id,
        String name,
        List<ChatGroupDto> groups
    ) {
        public ChatUserDto(User user) {
            this(
                user.getId(),
                user.getName(),
                user.getGroups().stream().map(ChatGroupDto::new).toList()
            );
        }
    }

    public record ChatGroupDto(
        long id,
        String name,
        String colour,
        String icon
    ) {
        public ChatGroupDto(Group group) {
            this(
                group.getId(),
                group.getName(),
                "",
                ""
            );
        }
    }
}
