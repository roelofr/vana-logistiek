package dev.roelofr.jobs;

import dev.roelofr.domains.users.GroupService;
import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.integrations.pocketid.PocketIdService;
import dev.roelofr.integrations.pocketid.model.PocketUser;
import dev.roelofr.service.FileService;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.util.StringUtil;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class UpdatePocketUsers {
    private final UserService userService;
    private final PocketIdService pocketIdService;
    private final FileService fileService;
    private final GroupService groupService;

    @Inject
    public UpdatePocketUsers(UserService userService, PocketIdService pocketIdService, FileService fileService, GroupService groupService) {
        this.userService = userService;
        this.pocketIdService = pocketIdService;
        this.fileService = fileService;
        this.groupService = groupService;
    }

    @Startup
    @Priority(6000)
    @Transactional
    public void createMissingPocketUsers() {
        if (!pocketIdService.isEnabled())
            return;

        pocketIdService.getAllUsers()
            .forEach(this::createFromPocket);
    }

    @Startup
    @Priority(6100)
    @Transactional
    public void updatePocketUsers() {
        if (!pocketIdService.isEnabled())
            return;

        userService.list()
            .forEach(this::downloadProfile);
    }

    void createFromPocket(PocketUser pocketUser) {
        var user = userService.findByProviderId(pocketUser.id())
            .orElseGet(() -> {
                var userEmail = pocketUser.email();
                if (StringUtil.isNullOrEmpty(userEmail))
                    userEmail = String.format("%s@login.troela.fun", pocketUser.id());

                var newUser = User.builder()
                    .providerId(pocketUser.id())
                    .name(pocketUser.displayName())
                    .email(userEmail)
                    .roles(pocketUser.userGroups().stream().map(PocketUser.UserGroup::name).collect(Collectors.toSet()))
                    .build();

                userService.save(newUser);

                log.info("Imported {} from Pocket", newUser.getName());

                return newUser;
            });

        var claims = pocketUser.customClaims().stream()
            .collect(Collectors.toMap(PocketUser.CustomClaim::key, PocketUser.CustomClaim::value));

        if (claims.containsKey("vana-default-group")) {
            var wantedGroup = groupService.findByLabel(claims.get("vana-default-group"));
            wantedGroup.ifPresent(user::addGroup);
        }

        if (user.getAvatar() == null)
            user.setAvatar(pocketIdService.getUserAvatar(user));
    }

    public void downloadProfile(User user) {
        if (!pocketIdService.isEnabled())
            return;

        log.debug("Downloading profile for {}", user.getName());

        var profileOpt = pocketIdService.getUser(user);
        if (profileOpt.isEmpty()) {
            log.warn("Failed to download profile of {}, while it should exist as ID {}", user.getName(), user.getProviderId());
            return;
        }

        var profile = profileOpt.get();
        for (var group : profile.userGroups()) {
            if (user.hasRole(group.name()))
                continue;

            user.addRole(group.name());
        }

        var avatarUrl = pocketIdService.getUserAvatar(user);
        user.setAvatar(avatarUrl);
    }
}
