package dev.roelofr.domains.users.tasks;

import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.model.UserFlags;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class RemoveActiveFlagsAtNight {
    private final UserService userService;

    @Inject
    public RemoveActiveFlagsAtNight(UserService userService) {
        this.userService = userService;
    }

    @Blocking
    @Scheduled(cron = "00 00 03 * * ?")
    void removeActiveFlagsForAllUsers() {
        for (var user : userService.listByFlag(UserFlags.Active)) {
            log.info("Remving active flag from {}", user.getName());
            user.removeFlag(UserFlags.Active);
        }
    }
}
