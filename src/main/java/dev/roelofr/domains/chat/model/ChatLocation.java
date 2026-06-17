package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A message a user has sent.
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(value = ChatLocation.TYPE)
public class ChatLocation extends AttributedChatEntry {
    public static final String TYPE = "location";

    @Column(name = "geo_lat")
    double latitude;

    @Column(name = "geo_long")
    double longitude;

    @Override
    @JsonInclude
    public String getType() {
        return TYPE;
    }
}
