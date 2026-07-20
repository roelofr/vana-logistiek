package dev.roelofr.domains.chat.model;

import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "chat_read_entries")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NamedQueries({
    @NamedQuery(
        name = "ChatReadEntry.findByUserForChats",
        query = """
                SELECT DISTINCT re
                FROM ChatReadEntry re
                LEFT JOIN FETCH re.entry
                LEFT JOIN FETCH re.chat chat
                LEFT JOIN re.user user
                WHERE chat.id IN :chats
                    AND user = :user
            """
    )
})
public class ChatReadEntry extends Model {
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    User user;

    @ManyToOne()
    @JoinColumn(name = "chat_id", nullable = false, updatable = false)
    Chat chat;

    @ManyToOne()
    @JoinColumn(name = "entry_id")
    ChatEntry entry;

    public Long getUserId() {
        return getUser().getId();
    }

    public Long getChatId() {
        return getChat().getId();
    }

    public Long getEntryId() {
        return getEntry().getId();
    }
}
