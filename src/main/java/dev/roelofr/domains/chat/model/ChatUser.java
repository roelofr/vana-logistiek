package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "chat_users")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Describes a user that is part of a chat.
 */
public class ChatUser extends ChatModel {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @JsonIncludeProperties({"id", "name"})
    User user;

    public static ChatUser create(Chat chat, User user) {
        return ChatUser.builder()
            .chat(chat)
            .user(user)
            .build();
    }
}
