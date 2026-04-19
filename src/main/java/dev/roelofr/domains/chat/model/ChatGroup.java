package dev.roelofr.domains.chat.model;

import dev.roelofr.domains.users.Group;
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
@Table(name = "chat_groups")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroup extends ChatModel {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false, updatable = false)
    Group group;
}
