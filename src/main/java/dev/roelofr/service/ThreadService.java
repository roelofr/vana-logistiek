package dev.roelofr.service;

import dev.roelofr.domain.Thread;
import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.UpdateType;
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

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final VendorRepository vendorRepository;

    private SecurityIdentity securityIdentity;
    private UserService userService;
    private ThreadUpdateRepository threadUpdateRepository;

    public List<Thread> findAll(boolean includeResolved) {
        if (includeResolved)
            return threadRepository.listAllSorted();

        return threadRepository.listUnresolvedSorted();
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
        return threadUpdateRepository.persistForType(type, thread, user, user.getTeam());
    }

    @Inject
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Inject
    public void setThreadUpdateRepository(ThreadUpdateRepository threadUpdateRepository) {
        this.threadUpdateRepository = threadUpdateRepository;
    }
}
