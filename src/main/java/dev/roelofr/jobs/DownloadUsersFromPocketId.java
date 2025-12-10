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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
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
    @Scheduled(every = "5M")
    void startUpdateUnlessTesting() {
        if (launchMode == LaunchMode.TEST)
            return;

        startUpdate();
    }

    void startUpdate() {
        if (!config.enabled())
            return;

        log.info("Starting PocketId Update...");

        fetchUsers();
    }

    private void fetchUsers() {
        var client = clientInstance.get();

        var loopsLeft = new AtomicInteger(10);
        var pageIndex = new AtomicInteger(0);

        do {
            var userResponse = client.getUsers(pageIndex.getAndIncrement());
            var pagination = userResponse.pagination();
            var users = userResponse.data();

            log.info("Retrieved page {} of {} from PocketID", pagination.currentPage(), pagination.totalPages());

            updateUserList(users);

            log.info("Processed {} users", users.size());

            if (!pagination.hasNext())
                break;
        } while (loopsLeft.getAndDecrement() > 0);

        log.info("Completed processing users!");
    }

    private void updateUserList(List<PocketUser> users) {
        for (var pocketUser : users) {
            var user = userRepository.findByProviderId(pocketUser.id()).orElse(null);
            if (user != null) {
                updateUser(user, pocketUser);
                log.debug("Updated user {} from provider ID {}", user.getId(), pocketUser.id());
            } else {
                createUser(pocketUser);
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

    private void createUser(PocketUser pocketUser) {
        var user = User.builder()
            .providerId(pocketUser.id())
            .build();

        updateUser(user, pocketUser);

        userRepository.persist(user);
    }
}
