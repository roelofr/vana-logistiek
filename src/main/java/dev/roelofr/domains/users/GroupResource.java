package dev.roelofr.domains.users;

import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.domains.users.dto.GroupListDto;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Authenticated
@Path("/groups")
@Tags({@Tag(name = "Users")})
@RequiredArgsConstructor
public class GroupResource {
    private final GroupService groupService;

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
}
