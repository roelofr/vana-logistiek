package dev.roelofr.integrations.pocketid;

import dev.roelofr.domains.users.model.User;
import dev.roelofr.integrations.pocketid.model.PocketUser;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ApplicationScoped
public class PocketIdService {
    private final PocketIdClient pocketIdClient;
    private final PocketIdConfig pocketIdConfig;

    public PocketIdService(@RestClient PocketIdClient pocketIdClient, PocketIdConfig pocketIdConfig) {
        this.pocketIdClient = pocketIdClient;
        this.pocketIdConfig = pocketIdConfig;
    }

    public boolean isEnabled() {
        return pocketIdConfig.enabled();
    }

    public List<PocketUser> getAllUsers() {
        var userList = new ArrayList<PocketUser>();
        var pageNumber = new AtomicInteger(1);
        var hasNextPage = true;

        do {
            log.info("Fetching page {} of users from Pocket ID", pageNumber.get());
            var results = pocketIdClient.getUsers(pageNumber.getAndIncrement());
            userList.addAll(results.data());
            hasNextPage = results.pagination().hasNextPage();
        } while (hasNextPage && pageNumber.get() < 50);

        return userList;
    }

    public Optional<PocketUser> getUser(User user) {
        try {
            return Optional.of(pocketIdClient.getUser(user.getProviderId()));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    public String getUserAvatar(User user) {
        if (user == null || user.getProviderId() == null)
            return null;

        return UriBuilder.fromUri(pocketIdConfig.url().orElse("/"))
            .path("/api/users/{id}/profile-picture.png")
            .buildFromMap(Map.of("id", user.getProviderId()))
            .toString();
    }

    public void setUserAvatar(User user, File avatar) throws PocketIdException {
        try {
            if (avatar == null) {
                log.info("Deleting profile picture of {} via ID {}", user.getName(), user.getProviderId());
                pocketIdClient.deleteUserProfilePicture(user.getProviderId());
            } else {
                log.info("Updating profile picture of {} via ID {}", user.getName(), user.getProviderId());
                pocketIdClient.setUserProfilePicture(user.getProviderId(), avatar);
            }
        } catch (HttpException e) {
            log.warn("Failed to update profile picture of {}: {}", user.getName(), e.getMessage());
            throw new PocketIdException("Failed to upload user avatar", e);
        }
    }
}
