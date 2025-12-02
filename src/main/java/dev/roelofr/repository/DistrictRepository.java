package dev.roelofr.repository;

import dev.roelofr.domain.Team;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class DistrictRepository implements PanacheRepository<Team> {
    public Optional<Team> findByName(String name) {
        return find("LOWER(name)", name)
            .firstResultOptional();
    }
}
