package dev.roelofr.domains.chat.model;

import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
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
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "chats")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "Chat.findWithoutKeyByUserSorted",
        query = """
                SELECT DISTINCT chat
                FROM Chat chat
                JOIN chat.users chatUsers
                JOIN chatUsers.user chatUser
                JOIN chat.groups chatGroups
                JOIN chatGroups.group as chatGroup
                JOIN chatGroup.users chatGroupUsers
                WHERE chat.key IS NULL
                    AND (chatUser = :user OR chatGroupUsers = :user)
                ORDER BY
                    /* CASE WHEN chat.closedAt != 'Closed' THEN 0 ELSE 1 END ASC, */
                    chat.updatedAt DESC
            """
    ),
    @NamedQuery(
        name = "Chat.countWithoutKeyByUser",
        query = """
                SELECT DISTINCT chat.id
                FROM Chat chat
                JOIN chat.users chatUsers
                JOIN chatUsers.user chatUser
                JOIN chat.groups chatGroups
                JOIN chatGroups.group as chatGroup
                JOIN chatGroup.users chatGroupUsers
                WHERE chat.key IS NULL
                    AND (chatUser = :user OR chatGroupUsers = :user)
            """
    )
})
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

    @Column(name = "chat_key", unique = true, updatable = false, length = 50)
    String key;

    @Builder.Default
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    ChatState state = ChatState.Active;

    @Builder.Default
    @OneToMany(mappedBy = "chat")
    List<ChatEntry> entries = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chat")
    List<ChatUser> users = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chat")
    List<ChatGroup> groups = new ArrayList<>();

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
}
