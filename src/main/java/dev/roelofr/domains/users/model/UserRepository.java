package dev.roelofr.domains.users.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private static final Locale dutchLocale = Locale.forLanguageTag("nl-NL");

    public User addUser(String username, String role) {
        var user = User.builder()
            .email(username)
            .roles(List.of(role))
            .build();

        persist(user);

        return user;
    }

    public List<User> mustFindByIds(@NotEmpty @NotNull List<@NotNull @Positive Long> ids) {
        var result = find("id in ?1", ids).list();
        if (result.size() != ids.size())
            throw new NoResultException(String.format(
                "Not all requested users were found, got %d / %d items",
                result.size(), ids.size()
            ));

        return result;
    }

    /**
     * Find a user by an email address
     */
    public Optional<User> findByEmailOptional(String email) {
        var normalEmail = email.trim().toLowerCase(dutchLocale);

        return find("LOWER(email) = ?1", normalEmail).firstResultOptional();
    }

    public Optional<User> findByProviderId(@NotNull String providerId) {
        return find("providerId", providerId).singleResultOptional();
    }

    public Optional<User> findByEmail(String mail) {
        return find("LOWER(email) = LOWER(?1)", mail).firstResultOptional();
    }
}
