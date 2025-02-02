package dev.roelofr.repository;

import dev.roelofr.domain.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private static final Locale dutchLocale = Locale.forLanguageTag("nl-NL");

    public Uni<User> addUser(String username, String password, String role) {
        return persist(
            User.builder()
                .username(username)
                .password(BcryptUtil.bcryptHash(password))
                .roles(List.of(role))
                .build()
        );
    }

    /**
     * Find a user by an email address
     *
     * @param email
     * @return
     */
    public Uni<Optional<User>> findByEmailOptional(String email) {
        var normalEmail = email.trim().toLowerCase(dutchLocale);

        return find("email = ?1", normalEmail).firstResult()
            .map(Optional::ofNullable);
    }
}
