package dev.roelofr.domains.chat.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.roelofr.domain.dto.Pagination;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatGroup;
import dev.roelofr.domains.chat.model.ChatUser;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;

import java.util.List;

@JsonPropertyOrder({"statistics", "chats"})
public record ChatList(
    Pagination statistics,
    List<ChatListChat> chats
) {

    public record ChatListChat(
        long id,
        String title,
        List<ChatListPartner> groups,
        List<ChatListPartner> users,
        boolean unread
    ) {
        public ChatListChat(Chat chat) {
            this(
                chat.getId(),
                chat.getTitle(),
                chat.getGroups().stream().map(ChatGroup::getGroup).map(ChatListPartner::new).toList(),
                chat.getUsers().stream().map(ChatUser::getUser).map(ChatListPartner::new).toList(),
                false
            );
        }
    }

    public record ChatListPartner(
        long id,
        String name
    ) {
        public ChatListPartner(Group group) {
            this(
                group.getId(),
                group.getName()
            );
        }

        public ChatListPartner(User user) {
            this(
                user.getId(),
                user.getName()
            );
        }
    }
}
