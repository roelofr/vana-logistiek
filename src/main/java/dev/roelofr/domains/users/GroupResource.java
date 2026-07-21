package dev.roelofr.domains.users;

import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.domains.users.dto.GroupListDto;
import dev.roelofr.domains.users.dto.SummaryGroupDto;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MultivaluedHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Authenticated
@Path("/groups")
@Tags({@Tag(name = "Users")})
@RequiredArgsConstructor
public class GroupResource {
    private final GroupService groupService;
    private final UserService userService;

    @GET
    @Path("/")
    @Transactional
    @JsonView(Views.Public.class)
    @Operation(operationId = "groupList", summary = "List all groups")
    public RestResponse<List<GroupListDto>> findAll() {
        return RestResponse.ok(
            groupService.listAll()
                .stream().map(GroupListDto::new)
                .toList()
        );
    }

    @GET
    @Path("/summary")
    @Transactional
    @Operation(operationId = "groupSummaryList", summary = "Lists all groups, their district(s) and all members")
    public RestResponse<List<SummaryGroupDto>> getSummary() {
        var groupList = groupService.listAllWithGroup();

        var groupUsers = new MultivaluedHashMap<Group, User>();
        userService.listAllWithGroups()
            .stream()
            .filter(user -> !user.getGroups().isEmpty())
            .forEach(user -> groupUsers.add(user.getGroup(), user));

        var groupDtos = new ArrayList<SummaryGroupDto>();

        for (var group : groupList) {
            var users = groupUsers.getOrDefault(group, List.of());

            groupDtos.add(
                new SummaryGroupDto(group, users)
            );
        }

        return RestResponse.ok(groupDtos);
    }
}
