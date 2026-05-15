package dev.roelofr.domains.chat.model;

import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
    public Chat(String title) {
        super();
        this.title = title;
    }

    public Chat(String title, String key) {
        super();
        this.title = title;
        this.key = key;
    }

    @Column(name = "title", nullable = false, length = 200)
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

    public void addGroup(@NotNull Group group) {
        if (groups.stream().anyMatch(chatGroup -> chatGroup.getGroup().is(group)))
            return;

        groups.add(ChatGroup.builder().chat(this).group(group).build());
    }

    public void addUser(@NotNull User user) {
        if (users.stream().anyMatch(chatUser -> chatUser.getUser().is(user)))
            return;

        users.add(ChatUser.builder().chat(this).user(user).build());
    }

    public boolean isVisibleForUser(@NotNull User user) {
        return users.stream().anyMatch(cu -> cu.user.is(user))
            || groups.stream().anyMatch(cg -> cg.group.hasUser(user));
    }
}
