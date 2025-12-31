package dev.roelofr.repository;

import dev.roelofr.domain.Team;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {
    public Optional<Team> findByName(@Nonnull String name) {
        return find("LOWER(name)", name.toLowerCase())
            .singleResultOptional();
    }

    public Team findSystemTeam(@Nonnull String name) {
        return find("name = ?1 and system = true", name)
            .singleResult();
    }
}
