package dev.roelofr.jobs;

import dev.roelofr.integrations.pocketid.PocketIdClient;
import dev.roelofr.integrations.pocketid.PocketIdConfig;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class DownloadUsersFromPocketId {
    @Inject
    PocketIdConfig config;

    @Inject
    @RestClient
    Instance<PocketIdClient> clientInstance;

    private PocketIdClient client = null;

    @Startup
    @Scheduled(every = "5M")
    @Blocking
    void startUpdate() {
        if (!config.enabled())
            return;

        if (client == null)
            client = clientInstance.get();

        log.info("Starting PocketId Update...");

        fetchUsers();
    }

    private void fetchUsers() {
        var users = client.getUsers();

        log.info("Got users {}", users);
    }
}
