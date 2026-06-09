package dev.roelofr.domains.issue;

import dev.roelofr.domains.chat.model.ChatSubject;
import dev.roelofr.domains.vendor.model.Vendor;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    @JoinColumn(name = "vendor_id", nullable = false, updatable = false)
    Vendor vendor;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(name = "resolved_at")
    LocalDateTime resolvedAt;
}
