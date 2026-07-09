package dev.roelofr.domains.chat.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.roelofr.domain.dto.Pagination;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.annotation.Nonnull;

import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({"statistics", "chats"})
public record ChatList(
    List<ChatListChat> chats,
    Pagination statistics
) {

    public record ChatListChat(
        @Nonnull Long id,
        @Nonnull String title,
        @Nonnull List<ChatListPartner> groups,
        @Nonnull List<ChatListPartner> users,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ChatSubjectDto subject,
        boolean unread
    ) {
        public ChatListChat(Chat chat) {
            this(
                chat.getId(),
                chat.getTitle(),
                chat.getGroups().stream().map(ChatListPartner::new).toList(),
                chat.getUsers().stream().map(ChatListPartner::new).toList(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                ChatSubjectDto.fromNullable(chat.getSubject()),
                false
            );
        }
    }

    public record ChatListPartner(
        @Nonnull Long id,
        @Nonnull String name,
        String avatar,
        String icon,
        String colour
    ) {
        public ChatListPartner(Group group) {
            this(
                group.getId(),
                group.getName(),
                null,
                group.getIcon(),
                group.getColour()
            );
        }

        public ChatListPartner(User user) {
            this(
                user.getId(),
                user.getName(),
                user.getAvatar(),
                null,
                null
            );
        }
    }
}
