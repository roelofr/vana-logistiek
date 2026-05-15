package dev.roelofr.domains.chat.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ChatEntryRepository implements PanacheRepository<ChatEntry> {
    public List<ChatEntry> getForChat(Chat chat) {
        return list("chat = ?1", Sort.by("created_at", Sort.Direction.Ascending).and("id", Sort.Direction.Ascending), chat);
    }
}
