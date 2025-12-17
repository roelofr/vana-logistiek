package dev.roelofr.service;

import dev.roelofr.domain.Thread;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.ThreadRepository;
import dev.roelofr.repository.VendorRepository;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Context;

import java.util.List;

@ApplicationScoped
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final SecurityIdentity securityIdentity;
    private final VendorRepository vendorRepository;

    public ThreadService(
        ThreadRepository threadRepository,
        @Context SecurityIdentity securityIdentity, VendorRepository vendorRepository) {
        this.threadRepository = threadRepository;
        this.securityIdentity = securityIdentity;
        this.vendorRepository = vendorRepository;
    }


    public List<Thread> findAll(boolean includeResolved) {
        if (includeResolved)
            return threadRepository.listAllSorted();

        return threadRepository.listUnresolvedSorted();
    }

    @Transactional
    public Thread createThread(
        @NotNull Vendor vendor,
        @NotBlank String name
    ) {
        var user = (User) securityIdentity.getPrincipal();
        var team = user.getTeam();

        var thread = Thread.builder()
            .user(user)
            .team(team)
            .vendor(vendor)
            .build();

        threadRepository.persist(thread);

        return thread;
    }
}
