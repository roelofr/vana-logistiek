package dev.roelofr.domains.chat.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ChatFileRepository implements PanacheRepository<ChatFile> {
    public Optional<ChatFile> findFile(long chatId, long fileId) {
        return find("chat.id = ?1 AND id = ?2", chatId, fileId).singleResultOptional();
    }

    public List<ChatFile> findUnprocessedFiles() {
        return list("fileStatus = ?1", FileStatus.New);
    }
}
