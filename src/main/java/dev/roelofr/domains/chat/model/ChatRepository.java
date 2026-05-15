package dev.roelofr.domains.chat.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChatRepository implements PanacheRepository<Chat> {
}
