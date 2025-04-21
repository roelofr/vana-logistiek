package dev.roelofr.rest.resources;

import dev.roelofr.openapi.GetMeResponse;
import dev.roelofr.openapi.PostLoginRequest;
import dev.roelofr.openapi.PostLoginResponse;
import dev.roelofr.service.AuthenticationService;
import dev.roelofr.service.AuthenticationService.ActingUser;
import io.quarkiverse.bucket4j.runtime.RateLimited;
import io.quarkiverse.bucket4j.runtime.resolver.IpResolver;
import io.quarkus.security.Authenticated;
import io.quarkus.security.UnauthorizedException;
import io.vertx.core.http.HttpServerRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@Path("/auth")
@RequiredArgsConstructor
public class AuthenticationResource {
    final AuthenticationService authenticationService;

    @GET
    @Path("/me")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "GetMe")
    public GetMeResponse getMe() {
        return null;
    }

    @GET
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "PostLogin")
    @RateLimited(bucket = "authentication", identityResolver = IpResolver.class)
    public PostLoginResponse postLogin(
        @Context HttpServerRequest request,
        @RequestBody @Valid PostLoginRequest postLoginRequest
    ) {
        var response = authenticationService.authenticate(
            ActingUser.fromRequest(request),
            postLoginRequest.getUsername(),
            postLoginRequest.getPassword()
        );

        if (!response.success()) {
            log.info("Login for {} failed: {}", postLoginRequest.getUsername(), response.reason());

            throw switch (response.reason()) {
                case SystemFailure -> new InternalServerErrorException();
                case AccountLocked -> new ForbiddenException();
                default -> new UnauthorizedException();
            };
        }

        return new PostLoginResponse()
            .name(response.username())
            .jwt(response.token())
            .expiration(response.tokenExpiration());
    }
}
