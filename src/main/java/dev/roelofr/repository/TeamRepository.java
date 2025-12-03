package dev.roelofr.repository;

import dev.roelofr.domain.Team;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {
    public Optional<Team> findBySlug(String slug) {
        return find("LOWER(slug)", slug)
            .firstResultOptional();
    }
}
