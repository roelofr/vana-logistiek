package dev.roelofr.repository;

import dev.roelofr.domain.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private static final Locale dutchLocale = Locale.forLanguageTag("nl-NL");

    public User addUser(String username, String password, String role) {
        var user = User.builder()
            .email(username)
            .password(BcryptUtil.bcryptHash(password))
            .roles(List.of(role))
            .build();

        persist(user);

        return user;
    }

    /**
     * Find a user by an email address
     *
     * @param email
     * @return
     */
    public Optional<User> findByEmailOptional(String email) {
        var normalEmail = email.trim().toLowerCase(dutchLocale);

        return find("LOWER(email) = ?1", normalEmail).firstResultOptional();
    }
}
