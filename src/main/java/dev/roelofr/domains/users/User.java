package dev.roelofr.domains.users;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.Model;
import io.quarkus.resteasy.reactive.jackson.SecureField;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
    @Column(columnDefinition = "json")
    List<String> roles = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    List<Group> groups;
}
