package dev.roelofr.domains.vendor.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class DistrictRepository implements PanacheRepository<District> {
    public Optional<District> findByName(String name) {
        return find("LOWER(name)", name)
            .firstResultOptional();
    }
}
