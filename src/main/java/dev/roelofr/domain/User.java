package dev.roelofr.domain;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
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
    @Builder.Default
    @Column(columnDefinition = "json")
    List<String> roles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;

    public void setAndEncryptPassword(String password) {
        this.setPassword(BcryptUtil.bcryptHash(password));
    }
}
