package dev.roelofr.domains.users.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GroupRepository implements PanacheRepository<Group> {
    public Optional<Group> findByName(String name) {
        if (name == null || name.isBlank())
            return Optional.empty();

        return find("LOWER(name) = LOWER(?1)", name).firstResultOptional();
    }

    public Optional<Group> findByLabel(String label) {
        return find("LOWER(label) = LOWER(?1)", label).singleResultOptional();
    }

    public Optional<Group> findLooselyByName(String name) {
        if (name == null || name.isBlank())
            return Optional.empty();

        return find("#Group.getLikeName", name).firstResultOptional();
    }

    public List<Group> mustFindByIds(@NotEmpty @NotNull List<@NotNull @Positive Long> ids) {
        var result = find("id in ?1", ids).list();
        if (result.size() != ids.size())
            throw new NoResultException(String.format(
                "Not all requested groups were found, got %d / %d items",
                result.size(), ids.size()
            ));

        return result;
    }
}
