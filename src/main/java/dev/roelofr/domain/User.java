package dev.roelofr.domain;

import dev.roelofr.domain.converters.JsonStringListConverter;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@UserDefinition
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends Model {
    @Column(length = 100)
    String name;

    @Username
    String email;

    @Password
    @Column(columnDefinition = "text")
    String password;

    @Builder.Default
    boolean active = false;

    @Roles
    @Column(columnDefinition = "json")
    @Convert(converter = JsonStringListConverter.class)
    List<String> roles;

    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;
}
