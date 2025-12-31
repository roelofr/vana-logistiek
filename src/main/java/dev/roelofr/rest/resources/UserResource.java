package dev.roelofr.rest.resources;

import dev.roelofr.domain.dto.UserDto;
import dev.roelofr.domain.dto.UserListDto;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.rest.responses.WhoamiResponse;
import dev.roelofr.service.UserService;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
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
    private final LaunchMode launchMode;

    @GET
    @Path("/")
    public RestResponse<List<UserListDto>> list() {
        return RestResponse.ok(userService.list());
    }

    @GET
    @Path("/me/token")
    public RestResponse<String> getMyToken(@Context SecurityIdentity securityIdentity) {
        if (launchMode.isProduction())
            return RestResponse.status(Response.Status.FORBIDDEN);

        var principal = securityIdentity.getPrincipal();
        if (principal instanceof JsonWebToken jwt)
            return RestResponse.ok(jwt.getRawToken());

        return RestResponse.status(RestResponse.Status.BAD_REQUEST);
    }

    @GET
    @Path("/me")
    public RestResponse<WhoamiResponse> getMe(@Context SecurityIdentity securityIdentity) {
        var principal = securityIdentity.getPrincipal();
        var user = userService.findBySecurityIdentity(securityIdentity);

        if (user.isEmpty())
            return RestResponse.notFound();

        var actualUser = user.get();
        log.info("Resolved user {} from principal {}", actualUser, securityIdentity.getPrincipal());

        if (launchMode.isDev() && principal instanceof JsonWebToken jwt)
            log.info("User [{}] logged in with JWT [{}]", actualUser.getName(), jwt.getRawToken());

        return RestResponse.ok(new WhoamiResponse(actualUser));
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
