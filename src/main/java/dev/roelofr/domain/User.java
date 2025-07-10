package dev.roelofr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends Model {
    @Column(length = 100)
    String name;

    String email;
    @Column(name = "provider_id", length = 50)
    String providerId;
    @Builder.Default
    boolean active = false;
    @Builder.Default
    @Column(columnDefinition = "json")
    List<String> roles = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;
}
