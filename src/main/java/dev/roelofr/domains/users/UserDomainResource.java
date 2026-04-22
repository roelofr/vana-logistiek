package dev.roelofr.domains.users;

import com.fasterxml.jackson.annotation.JsonView;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Authenticated
@Path("/users")
@RequiredArgsConstructor
public class UserDomainResource {
    private final UserService userService;

    @GET
    @Path("/me")
    @Transactional
    @JsonView(Views.Private.class)
    public RestResponse<User> findMe(@Context JsonWebToken jwt) {
        return userService.findByPrincipal(jwt)
            .map(RestResponse::ok)
            .orElseGet(RestResponse::notFound);
    }

    @GET
    @Path("/")
    @Transactional
    @JsonView(Views.Public.class)
    public RestResponse<List<User>> findAll() {
        return RestResponse.ok(
            userService.listAll()
        );
    }

    @GET
    @Path("/{id}")
    @Transactional
    @JsonView(Views.Public.class)
    public RestResponse<User> findById(@PathParam("id") long id) {
        return userService.findById(id)
            .map(RestResponse::ok)
            .orElseGet(RestResponse::notFound);
    }
}
