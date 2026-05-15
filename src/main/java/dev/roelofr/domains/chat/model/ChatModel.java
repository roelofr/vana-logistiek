package dev.roelofr.domains.chat.model;

import dev.roelofr.domain.Model;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
/**
 * An entry that interacts with a chat, in any kind of way.
 * These include:
 * - Users linked to a chat
 * - Groups linked to a chat
 * - Entries posted in a chat (messages, files, etc)
 */
public abstract class ChatModel extends Model {
    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", updatable = false, nullable = false)
    Chat chat;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}
