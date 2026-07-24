package dev.roelofr.domains.users;

import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.Roles;
import dev.roelofr.domains.users.dto.ActiveUserDto;
import dev.roelofr.domains.users.dto.OnboardRequest;
import dev.roelofr.domains.users.dto.SetActiveRequest;
import dev.roelofr.domains.users.dto.SetUserGroupsRequest;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserFlags;
import dev.roelofr.integrations.pocketid.PocketIdException;
import dev.roelofr.integrations.pocketid.PocketIdService;
import dev.roelofr.rest.resources.ProfileResource;
import dev.roelofr.service.FileService;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Authenticated
@Path("/users")
@Tags({@Tag(name = "Users")})
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final GroupService groupService;
    private final ProfileResource profileResource;
    private final FileService fileService;
    private final PocketIdService pocketIdService;

    @GET
    @Path("/me")
    @Transactional
    @JsonView(Views.Private.class)
    @Operation(operationId = "userGetMe", summary = "Get information about the current user")
    public RestResponse<ActiveUserDto> getMe(@Context User user) {
        var userGroups = user.getGroups();
        return RestResponse.ok(new ActiveUserDto(user, userGroups));
    }

    @POST
    @Path("/me/active")
    @Transactional
    @Operation(operationId = "userSetActive", summary = "Sets if the current user is active at this moment")
    public RestResponse<ActiveUserDto> postActive(@Context User user, @Valid SetActiveRequest request) {
        var freshUser = userService.findById(user.getId());

        freshUser.setFlag(UserFlags.Active, request.isActive());

        return RestResponse.ok();
    }

    @POST
    @Transactional
    @Path("/me/onboard")
    @Operation(operationId = "userOnboardMe", summary = "Onboard the given user, letting them choose their own group")
    public RestResponse<Void> onboardMe(@Context User user, OnboardRequest request) {
        if (user.hasFlag(UserFlags.Onboarded))
            return RestResponse.status(RestResponse.Status.FORBIDDEN);

        if (!fileService.isValidImage(request.picture()))
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);

        var writeableUser = userService.findById(user.getId());
        if (writeableUser == null) {
            log.error("Failed to onboard {}, user not found by ID", user.getName());
            return RestResponse.serverError();
        }

        if (request.groupId() != null) {
            var wantedGroup = groupService.findById(request.groupId());
            if (wantedGroup == null)
                return RestResponse.status(RestResponse.Status.NOT_FOUND);

            if (user.getGroup() != null)
                return RestResponse.status(RestResponse.Status.CONFLICT);

            log.info("Adding user {} to group {}", user.getName(), wantedGroup.getName());
            writeableUser.setGroups(Set.of(wantedGroup));
        }

        try {
            log.info("Uploading new profile picture for {}", user.getName());
            pocketIdService.setUserAvatar(user, request.picture());

            log.info("Downloading updated user profile");
            var avatarUrl = pocketIdService.getUserAvatar(user);
            writeableUser.setAvatar(avatarUrl);
        } catch (PocketIdException | RuntimeException e) {
            log.warn("Failed to set profile on user");
            QuarkusTransaction.rollback();

            return RestResponse.serverError();
        }

        log.info("Onboarding of user {} complete!", user.getName());

        writeableUser.addFlag(UserFlags.Onboarded);

        return RestResponse.ok();
    }

    @GET
    @Path("/")
    @Transactional
    @JsonView(Views.Public.class)
    @Operation(operationId = "userList", summary = "List all users")
    public RestResponse<List<User>> findAll() {
        return RestResponse.ok(
            userService.listAllWithGroups()
        );
    }

    @GET
    @Path("/{id}")
    @Transactional
    @JsonView(Views.Public.class)
    @Operation(operationId = "userFindById", summary = "Show a single user")
    public RestResponse<User> findById(@PathParam("id") long id) {
        return Optional.ofNullable(userService.findById(id))
            .map(RestResponse::ok)
            .orElseGet(RestResponse::notFound);
    }

    @PATCH
    @Transactional
    @Path("/{id}/groups")
    @RolesAllowed({Roles.Admin})
    @Operation(operationId = "userSetGroup", summary = "Sets the groups the user belongs to")
    public RestResponse<Void> findById(@PathParam("id") long id, @Valid SetUserGroupsRequest request) {
        var wantedUser = userService.findById(id);
        if (wantedUser == null)
            return RestResponse.notFound();

        var wantedGroups = groupService.findByIds(request.groups());
        if (wantedGroups.size() != request.groups().size())
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);

        wantedUser.setGroups(wantedGroups);

        return RestResponse.ok();
    }

    @PATCH
    @Transactional
    @Path("/{id}/roles")
    @RolesAllowed({Roles.Admin})
    @Operation(operationId = "userSetRoles", summary = "Sets the roles the user has")
    public RestResponse<Void> findById(@PathParam("id") long id, @Valid @NotEmpty List<@NotBlank String> roles) {
        var wantedUser = userService.findById(id);
        if (wantedUser == null)
            return RestResponse.notFound();

        wantedUser.setRoles(Set.copyOf(roles));

        return RestResponse.ok();
    }
}
