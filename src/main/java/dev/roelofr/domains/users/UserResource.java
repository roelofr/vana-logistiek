package dev.roelofr.domains.users;

import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.domains.users.dto.SetUserGroupsRequest;
import dev.roelofr.domains.users.model.User;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
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

@Slf4j
@Authenticated
@Path("/users")
@Tags({@Tag(name = "Users")})
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final GroupService groupService;

    @GET
    @Path("/me")
    @Transactional
    @JsonView(Views.Private.class)
    @Operation(operationId = "userFindMe", summary = "Find the current user")
    public RestResponse<User> findMe(@Context User user) {
        return RestResponse.ok(user);
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
}
