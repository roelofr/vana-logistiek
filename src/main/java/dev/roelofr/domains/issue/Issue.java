package dev.roelofr.domains.issue;

import dev.roelofr.domain.dto.Location;
import dev.roelofr.domains.chat.model.ChatSubject;
import dev.roelofr.domains.vendor.model.Vendor;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "issues")
@DiscriminatorValue("issue")
@EqualsAndHashCode(callSuper = true)
public class Issue extends ChatSubject {
    @ManyToOne()
    @JoinColumn(name = "vendor_id", updatable = false)
    Vendor vendor;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "geo_lat", columnDefinition = "DOUBLE", updatable = false)
    Double locationLat;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "geo_lng", columnDefinition = "DOUBLE", updatable = false)
    Double locationLong;

    @Transient
    @Setter(AccessLevel.NONE)
    Location location;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(name = "resolved_at")
    LocalDateTime resolvedAt;

    @PostLoad
    void assignLocationFromLatLng() {
        if (locationLat != null && locationLong != null)
            location = new Location(locationLat, locationLong);
    }

    @PrePersist
    void assignLatLngFromLocation() {
        if (location != null) {
            locationLat = location.lat();
            locationLong = location.lng();
        }
    }

    public void setLocation(Location location) {
        if (this.getId() != null)
            throw new IllegalStateException("Cannot set location on an existing issue.");
        this.location = location;
    }
}
