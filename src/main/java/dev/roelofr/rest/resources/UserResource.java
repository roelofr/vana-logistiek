package dev.roelofr.rest.resources;

import dev.roelofr.domain.dto.UserDto;
import dev.roelofr.domain.dto.UserListDto;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.rest.responses.WhoamiResponse;
import dev.roelofr.service.UserService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Authenticated
@Path("/users")
@Tag(name = "Users")
@RequiredArgsConstructor
public class UserResource {
    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final UserService userService;

    @GET
    @Path("/")
    public RestResponse<List<UserListDto>> list() {
        return RestResponse.ok(userService.list());
    }

    @GET
    @Path("/me")
    public RestResponse<WhoamiResponse> getMe(@Context SecurityIdentity securityIdentity) {
        var principal = securityIdentity.getPrincipal();
        if (principal == null)
            return RestResponse.notFound();

        log.info("Principal name = {}", principal.getName());

        var user = userRepository.findByProviderId(principal.getName());
        return RestResponse.status(Response.Status.NOT_IMPLEMENTED);
    }

    @GET
    @Path("/{id}")
    public RestResponse<UserDto> findUserById(@PathParam("id") Long id) {
        var result = userRepository.find("id", id)
            .project(UserDto.class)
            .firstResultOptional();

        if (result.isEmpty())
            return RestResponse.notFound();

        return RestResponse.ok(result.get());
    }
}
