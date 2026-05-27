package dev.roelofr.domains.chat.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@ApplicationScoped
public class ChatRepository implements PanacheRepository<Chat> {
    public Optional<Chat> findByKey(@NotNull String key) {
        return find("LOWER(key) = LOWER(?1)", key).singleResultOptional();
    }
}
