package dev.roelofr.jobs;

import dev.roelofr.domain.enums.UpdateType;
import dev.roelofr.repository.ThreadRepository;
import dev.roelofr.service.ThreadService;
import io.quarkus.runtime.Startup;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CreateProperThreadAttachments {
    private final ThreadRepository threadRepository;
    private final ThreadService threadService;

    @Startup
    @Transactional
    void createMissingAttachments() {
        threadRepository.stream("id not in (select distinct thread.id from ThreadUpdate)")
            .forEach(thread -> {
                log.info("Thread [{}: {}] has no attachments", thread.getId(), thread.getSubject());

                threadService.createUpdate(thread, UpdateType.Created, thread.getUser());
            });
    }
}
