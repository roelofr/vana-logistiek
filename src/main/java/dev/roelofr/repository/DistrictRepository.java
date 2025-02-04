package dev.roelofr.repository;

import dev.roelofr.domain.District;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import static dev.roelofr.Constants.Dutch;

@ApplicationScoped
public class DistrictRepository implements PanacheRepository<District> {
    public Uni<District> findByName(String name) {
        return find("LOWER(name)", name.toLowerCase(Dutch))
            .firstResult();
    }
}
