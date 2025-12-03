package dev.roelofr.repository;

import dev.roelofr.domain.Thread;
import dev.roelofr.domain.Vendor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ThreadRepository implements PanacheRepository<Thread> {
    public List<Thread> findByVendor(@Nonnull Vendor vendor) {
        return find("#findByVendor", vendor).list();
    }

    public Optional<Thread> findBySlug(String slug) {
        return find("LOWER(slug)", slug)
            .firstResultOptional();
    }

    public Optional<Thread> findByName(String name) {
        return find("LOWER(name)", name)
            .firstResultOptional();
    }
}
