package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A message the system has sent
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(value = ChatSystemMessage.TYPE)
public class ChatSystemMessage extends ChatEntry {
    public static final String TYPE = "system";

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 40)
    public SystemMessageType messageType;

    @Lob
    @Column(name = "message", columnDefinition = "text")
    public String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", updatable = false)
    @JsonIncludeProperties({"id", "providerId", "avatar", "name"})
    public User subjectUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", updatable = false)
    @JsonIncludeProperties({"id", "icon", "colour", "name"})
    public Group subjectGroup;

    @Override
    @JsonInclude
    public String getType() {
        return TYPE;
    }
}
