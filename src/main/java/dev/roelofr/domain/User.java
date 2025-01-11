package dev.roelofr.domain;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@UserDefinition
public class User extends Model {
    @Username
    public String username;

    @Password
    public String password;

    @Roles
    public String role;

    /**
     * Adds a new user to the database
     *
     * @param username the username
     * @param password the unencrypted password (it is encrypted with bcrypt)
     * @param role     the comma-separated roles
     */
    public static User add(String username, String password, String role) {
        User user = new User();

        user.username = username;
        user.password = BcryptUtil.bcryptHash(password);
        user.role = role;

        user.persist();

        return user;
    }
}
