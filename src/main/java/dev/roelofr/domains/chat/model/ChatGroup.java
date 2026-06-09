package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import dev.roelofr.domains.users.model.Group;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Describes a group that is part of a chat.
 */
@Data
@Entity
@Table(name = "chat_groups")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroup extends ChatModel {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false, updatable = false)
    @JsonIncludeProperties({"id", "name"})
    Group group;

    public static ChatGroup create(Chat chat, Group group) {
        return ChatGroup.builder()
            .chat(chat)
            .group(group)
            .build();
    }
}
