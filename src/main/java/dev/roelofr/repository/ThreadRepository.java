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

    public Optional<Thread> findByIdWithAllRelations(long id) {
        return find("Thread.findByIdWithAllRelations", id).singleResultOptional();
    }

    public Optional<Thread> findBySlug(String slug) {
        return find("LOWER(slug) = LOWER(?1)", slug)
            .firstResultOptional();
    }

    public Optional<Thread> findByName(String name) {
        return find("LOWER(name) = LOWER(?1)", name)
            .firstResultOptional();
    }

    public List<Thread> listUnresolvedSorted() {
        return list("#Thread.unresolvedSorted");
    }

    public List<Thread> listAllSorted() {
        return list("#Thread.allSorted");
    }
}
