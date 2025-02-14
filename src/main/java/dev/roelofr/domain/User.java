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
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@UserDefinition
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends Model {
    @Column(length = 100)
    public String name;

    @Username
    public String email;

    @Password
    @Column(columnDefinition = "text")
    public String password;

    @Roles
    @Column(columnDefinition = "json")
    @Convert(converter = JsonStringListConverter.class)
    public List<String> roles;

    @ManyToOne
    @JoinColumn(name = "district_id")
    public District district;
}
