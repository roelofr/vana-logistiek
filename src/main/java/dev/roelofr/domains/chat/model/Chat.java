package dev.roelofr.domains.chat.model;

import dev.roelofr.domain.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "chats")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends Model {
    @Column(name = "title", nullable = false, length = 100)
    String title;

    @Column(name = "key", unique = true, updatable = false, length = 50)
    String key;

    @Builder.Default
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    ChatState state = ChatState.Active;

    @OneToMany(mappedBy = "chat")
    List<ChatEntry> entries;

    @OneToMany(mappedBy = "chat")
    List<ChatUser> users;

    @OneToMany(mappedBy = "chat")
    List<ChatGroup> groups;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
