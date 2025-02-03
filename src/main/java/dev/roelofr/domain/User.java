package dev.roelofr.domain;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@UserDefinition
public class User extends Model {
    public String name;

    @Username
    public String email;

    @Password
    public String password;

    @Roles
    public List<String> roles;

    @ManyToOne
    @JoinColumn(name = "district_id")
    public District district;
}
