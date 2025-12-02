package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.roelofr.config.Roles;
import io.quarkus.resteasy.reactive.jackson.SecureField;
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
    @Column(name = "provider_id", length = 50)
    String providerId;

    @Column(length = 100)
    String name;

    @SecureField(rolesAllowed = {Roles.Admin})
    @Column(length = 100)
    String email;

    @Builder.Default
    boolean active = false;

    @Builder.Default
    @Column(columnDefinition = "json")
    List<String> roles = new ArrayList<>();

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "team_id")
    Team team;
}
