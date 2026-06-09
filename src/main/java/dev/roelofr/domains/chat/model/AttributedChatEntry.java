package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A chat entry that can be attributed to a user.
 */
@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class AttributedChatEntry extends ChatEntry {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", updatable = false)
    @JsonIncludeProperties({"id", "name"})
    public User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", updatable = false)
    @JsonIncludeProperties({"id", "name"})
    public Group group;
}
