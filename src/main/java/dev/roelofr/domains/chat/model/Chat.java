package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@NamedQueries({
    @NamedQuery(
        name = "Chat.findByUserSorted",
        query = """
                SELECT DISTINCT chat
                FROM Chat chat
                LEFT JOIN chat.users chatUser
                LEFT JOIN chat.groups chatGroup
                LEFT JOIN chatGroup.users chatGroupUsers
                LEFT JOIN FETCH chat.subject
                WHERE (chatUser = :user OR chatGroupUsers = :user)
                ORDER BY
                    /* CASE WHEN chat.closedAt != 'Closed' THEN 0 ELSE 1 END ASC, */
                    chat.updatedAt DESC
            """
    ),
    @NamedQuery(
        name = "Chat.countByUser",
        query = """
                SELECT DISTINCT chat.id
                FROM Chat chat
                LEFT JOIN chat.users chatUser
                LEFT JOIN chat.groups chatGroup
                LEFT JOIN chatGroup.users chatGroupUsers
                WHERE (chatUser = :user OR chatGroupUsers = :user)
            """
    ),
    @NamedQuery(
        name = "Chat.findIdsByUser",
        query = """
                SELECT DISTINCT chat.id
                FROM Chat chat
                LEFT JOIN chat.users chatUser
                LEFT JOIN chat.groups chatGroup
                LEFT JOIN chatGroup.users chatGroupUsers
                WHERE (chatUser = :user OR chatGroupUsers = :user)
            """
    ),
    @NamedQuery(
        name = "Chat.getChatsWithUsersById",
        query = """
                SELECT chat
                FROM Chat chat
                LEFT JOIN FETCH chat.users
                WHERE chat.id IN (:ids)
            """
    ),
    @NamedQuery(
        name = "Chat.getChatsWithGroupsById",
        query = """
                SELECT chat
                FROM Chat chat
                LEFT JOIN FETCH chat.groups
                WHERE chat.id IN (:ids)
            """
    )
})
public class Chat extends Model {
    @Column(name = "title", nullable = false, length = 200)
    String title;
    @JsonIgnore
    @Column(name = "chat_key", unique = true, updatable = false, length = 50)
    String key;
    @Builder.Default
    @Column(name = "chat_type", updatable = false, length = 20)
    @Enumerated(EnumType.STRING)
    ChatType type = ChatType.Regular;
    @Builder.Default
    @Column(name = "chat_state", length = 16)
    @Enumerated(EnumType.STRING)
    ChatState state = ChatState.Active;
    @OneToOne(mappedBy = "chat")
    ChatSubject subject;
    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "chat")
    List<ChatEntry> entries = new ArrayList<>();
    @ManyToMany
    @JoinTable(
        name = "chat_users",
        joinColumns = @JoinColumn(name = "chat_id"), inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    @JsonIncludeProperties({"id", "providerId", "name", "avatar"})
    List<User> users = new ArrayList<>();
    @ManyToMany
    @JoinTable(
        name = "chat_groups",
        joinColumns = @JoinColumn(name = "chat_id"), inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @Builder.Default
    @JsonIncludeProperties({"id", "name", "icon", "colour"})
    List<Group> groups = new ArrayList<>();
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    public static Chat create(String title) {
        return builder().title(title).build();
    }

    public static Chat create(String title, String key) {
        return builder().title(title).key(key).build();
    }

    @PrePersist
    void ensureConstraintsPrePersist() {
        if (type == ChatType.Group)
            state = ChatState.Permanent;
    }

    private void ensureChatIsMutable() {
        // Not-persisted chats are always mutable.
        if (getId() == null || createdAt == null)
            return;

        if (type == ChatType.Group) {
            var justNow = LocalDateTime.now().minusSeconds(30);
            if (createdAt.isBefore(justNow))
                throw new RuntimeException("Chat cannot be mutated anymore");
        }
    }

    public void addGroup(@NotNull Group group) {
        ensureChatIsMutable();

        if (groups.stream().anyMatch(chatGroup -> chatGroup.is(group)))
            return;

        groups.add(group);
    }

    public void addUser(@NotNull User user) {
        ensureChatIsMutable();

        if (users.stream().anyMatch(chatUser -> chatUser.is(user)))
            return;

        users.add(user);
    }

    public boolean isClosed() {
        return state == ChatState.Closed;
    }
}
