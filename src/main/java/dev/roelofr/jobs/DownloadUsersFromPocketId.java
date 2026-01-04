package dev.roelofr.jobs;

import dev.roelofr.domain.User;
import dev.roelofr.integrations.pocketid.PocketIdClient;
import dev.roelofr.integrations.pocketid.PocketIdConfig;
import dev.roelofr.integrations.pocketid.model.PocketUser;
import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ApplicationScoped
public class DownloadUsersFromPocketId {
    @Inject
    PocketIdConfig config;

    @Inject
    LaunchMode launchMode;

    @Inject
    @RestClient
    Instance<PocketIdClient> clientInstance;

    @Inject
    UserRepository userRepository;

    @Startup
    @Blocking
    @Priority(Priorities.Download)
    @Scheduled(every = "5M", delayed = "5M")
    void startUpdateUnlessTesting() {
        if (launchMode == LaunchMode.TEST)
            return;

        startUpdate();
    }

    @Transactional
    void startUpdate() {
        if (!config.enabled())
            return;

        log.info("Starting PocketId Update...");

        try {
            fetchUsers();
        } catch (WebApplicationException error) {
            final var statusCode = error.getResponse().getStatusInfo().toEnum();
            if (statusCode == Status.FORBIDDEN || statusCode == Status.UNAUTHORIZED) {
                log.error("PocketID API configuration failure: {} {}", statusCode.getStatusCode(), statusCode.getReasonPhrase());
            } else
                throw error;
        }
    }

    private void fetchUsers() {
        var client = clientInstance.get();

        var loopsLeft = new AtomicInteger(10);
        var pageIndex = new AtomicInteger(0);

        do {
            var userResponse = client.getUsers(pageIndex.getAndIncrement());
            var pagination = userResponse.pagination();
            var users = userResponse.data();

            log.debug("Retrieved page {} of {} from PocketID", pagination.currentPage(), pagination.totalPages());

            updateUserList(users);

            log.debug("Processed {} users", users.size());

            if (!pagination.hasNext())
                break;
        } while (loopsLeft.getAndDecrement() > 0);

        log.info("Completed processing users!");
    }

    private void updateUserList(List<PocketUser> users) {
        for (var pocketUser : users) {
            var userOptional = userRepository.findByProviderId(pocketUser.id());

            if (userOptional.isPresent()) {
                var user = userOptional.get();
                updateUser(user, pocketUser);
                log.debug("Updated user {} from provider ID {}", user, pocketUser.id());
            } else {
                var user = createUser(pocketUser);
                log.debug("Created user {} from provider ID {}", user, pocketUser.id());
            }
        }
    }

    private void updateUser(User user, PocketUser pocketUser) {
        user.setName(pocketUser.displayName());
        user.setEmail(pocketUser.email());
        user.setRoles(
            pocketUser.userGroups().stream()
                .map(PocketUser.UserGroup::name)
                .toList()
        );
    }

    private User createUser(PocketUser pocketUser) {
        var user = User.builder()
            .providerId(pocketUser.id())
            .build();

        updateUser(user, pocketUser);

        userRepository.persist(user);

        return user;
    }
}
