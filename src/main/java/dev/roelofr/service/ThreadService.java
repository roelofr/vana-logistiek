package dev.roelofr.service;

import dev.roelofr.AppUtil;
import dev.roelofr.Events;
import dev.roelofr.config.AppConfig;
import dev.roelofr.domain.Thread;
import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.FileStatus;
import dev.roelofr.domain.enums.UpdateType;
import dev.roelofr.domain.projections.ListThread;
import dev.roelofr.repository.ThreadRepository;
import dev.roelofr.repository.ThreadUpdateRepository;
import dev.roelofr.repository.VendorRepository;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

import static dev.roelofr.AppUtil.cleanupFilename;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final VendorRepository vendorRepository;

    @Inject
    @Channel(Events.ThreadUpdateCreated)
    Emitter<ThreadUpdate> threadUpdateCreatedEmitter;

    private SecurityIdentity securityIdentity;
    private UserService userService;
    private ThreadUpdateRepository threadUpdateRepository;
    private AppConfig appConfig;
    private FileService fileService;

    public List<ListThread> findAll(boolean includeResolved) {
        return (includeResolved
            ? threadRepository.listAllSorted()
            : threadRepository.listUnresolvedSorted())
            .project(ListThread.class)
            .list();
    }

    @Inject
    void injectContextBasedObjects(@Context SecurityIdentity securityIdentity) {
        this.securityIdentity = securityIdentity;
    }

    @Transactional
    public Thread createThread(
        @NotNull Vendor vendor,
        @NotBlank String subject
    ) {
        var userOpt = userService.findBySecurityIdentity(securityIdentity);
        if (userOpt.isEmpty())
            throw new InternalServerErrorException("User authenticated but not registered.");

        var user = userOpt.get();
        var team = user.getTeam();

        var thread = Thread.builder()
            .user(user)
            .team(team)
            .vendor(vendor)
            .subject(subject)
            .build();

        log.info("Persisting thread {}", thread);

        threadRepository.persist(thread);

        return thread;
    }

    @Transactional
    public ThreadUpdate createUpdate(@NotNull Thread thread, @NotNull UpdateType type) {
        var user = userService.findBySecurityIdentity(securityIdentity).orElse(null);
        if (user == null)
            throw new RuntimeException("No user is present!");

        return createUpdate(thread, type, user);
    }

    @Transactional
    public ThreadUpdate createUpdate(@NotNull Thread thread, @NotNull UpdateType type, @NotNull User user) {
        var update = threadUpdateRepository.persistForType(type, thread, user, user.getTeam());
        threadUpdateCreatedEmitter.send(update);
        return update;
    }

    @Transactional
    public ThreadUpdate createAttachmentUpdate(@NotNull Thread thread, @NotNull FileUpload file) {
        var user = userService.findBySecurityIdentity(securityIdentity).orElse(null);
        if (user == null)
            throw new RuntimeException("No user is present!");

        return createAttachmentUpdate(thread, file, user);
    }

    @Transactional
    public ThreadUpdate createAttachmentUpdate(@NotNull Thread thread, @NotNull FileUpload file, @NotNull User user) {
        var createdFile = fileService.persistUpload(file);

        var attachment = (ThreadUpdate.ThreadAttachment) createUpdate(thread, UpdateType.Attachment, user);
        attachment.setFilePath(createdFile);
        attachment.setFilename(cleanupFilename(file.fileName(), 100));
        attachment.setFileStatus(FileStatus.New);

        // TODO Fire event

        return attachment;
    }

    @Inject
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Inject
    public void setThreadUpdateRepository(ThreadUpdateRepository threadUpdateRepository) {
        this.threadUpdateRepository = threadUpdateRepository;
    }

    @Inject
    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Inject
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
}
