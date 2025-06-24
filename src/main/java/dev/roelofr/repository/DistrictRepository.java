package dev.roelofr.repository;

import dev.roelofr.domain.District;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

import static dev.roelofr.Constants.LocaleDutch;

@ApplicationScoped
public class DistrictRepository implements PanacheRepository<District> {
    public Optional<District> findByName(String name) {
        return find("LOWER(name)", name.toLowerCase(LocaleDutch))
            .firstResultOptional();
    }
}
