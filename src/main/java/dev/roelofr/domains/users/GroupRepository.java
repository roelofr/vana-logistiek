package dev.roelofr.domains.users;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class GroupRepository implements PanacheRepository<Group> {
    public Optional<Group> findByName(String name) {
        if (name == null || name.isBlank())
            return Optional.empty();

        return find("LOWER(name) = LOWER(?1)", name).firstResultOptional();
    }
}
