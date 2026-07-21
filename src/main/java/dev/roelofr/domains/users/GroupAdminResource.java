package dev.roelofr.domains.users;

import dev.roelofr.config.Roles;
import dev.roelofr.domains.users.dto.GroupAdminRequest;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import io.quarkus.runtime.util.StringUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

@Slf4j
@RequiredArgsConstructor
@Path("/admin/groups")
@RolesAllowed(Roles.Admin)
public class GroupAdminResource {
    private final static String DEFAULT_COLOUR = "slate";
    private final static String DEFAULT_ICON = "volleyball";

    private final GroupService groupService;

    @POST
    @Transactional
    public RestResponse<Void> postGroup(@Context User user, @Valid GroupAdminRequest request) {
        log.info("User {} wants to create group {}", user.getName(), request.name());

        var existingGroup = groupService.findByName(request.name());
        if (existingGroup.isPresent()) {
            log.warn("Cannot create group {} (as requested by {}), it already exists", request.name(), user.getName());
            return RestResponse.status(Response.Status.CONFLICT);
        }

        var newGroup = Group.builder()
            .name(request.name())
            .icon(request.icon())
            .colour(request.colour())
            .build();

        if (StringUtil.isNullOrEmpty(newGroup.getColour()))
            newGroup.setColour(DEFAULT_COLOUR);

        if (StringUtil.isNullOrEmpty(newGroup.getIcon()))
            newGroup.setIcon(DEFAULT_ICON);

        groupService.save(newGroup);

        log.info("User {} created group {} (#{})", user.getName(), newGroup.getName(), newGroup.getId());

        return RestResponse.ok();
    }

    @POST
    @Transactional
    @Path("/{id}")
    public RestResponse<Void> postGroupById(@Context User user, @PathParam("id") @Positive long groupId, @Valid GroupAdminRequest request) {
        log.info("User {} wants to update group {}", user.getName(), groupId);

        var group = groupService.findById(groupId);
        if (group == null) {
            log.warn("Cannot update group {} (as requested by {}), it was not found", groupId, user.getName());
            return RestResponse.notFound();
        }

        var newGroup = groupService.findByName(request.name());
        if (newGroup.isPresent() && !newGroup.get().equals(group)) {
            log.warn("Cannot set name of group {} to {} (as requested by {}), it already exists", group.getName(), request.name(), user.getName());
            return RestResponse.status(Response.Status.CONFLICT);
        }

        group.setName(request.name());
        group.setIcon(request.icon());
        group.setColour(request.colour());

        if (StringUtil.isNullOrEmpty(group.getColour()))
            group.setColour(DEFAULT_COLOUR);

        if (StringUtil.isNullOrEmpty(group.getIcon()))
            group.setIcon(DEFAULT_ICON);

        log.info("User {} updated group {} (#{})", user.getName(), group.getName(), group.getId());

        return RestResponse.ok();
    }
}
