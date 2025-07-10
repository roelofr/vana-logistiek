package dev.roelofr.rest.resources;

import dev.roelofr.domain.rest.AuthConsumeResponse;
import dev.roelofr.rest.request.AuthConsumeRequest;
import dev.roelofr.rest.responses.AuthMeResponse;
import dev.roelofr.service.AuthenticationService;
import dev.roelofr.service.JwtSubjectUserCache;
import dev.roelofr.service.UserService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.UnauthorizedException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.RestResponse;

@Slf4j
@Path("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationResource {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final JwtSubjectUserCache jwtSubjectUserCache;

    @HEAD
    @NoCache
    @Authenticated
    @Operation(
        operationId = "verify",
        summary = "Validates the session for the user, return 401 or 403 if failing"
    )
    public Response verify(@Context SecurityContext securityContext) {
        try {
            var user = userService.fromPrincipal(securityContext.getUserPrincipal());
            log.info("Auth verify OK for {}", user.getName());

            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.UNAUTHORIZED).build();
        } catch (IllegalStateException e) {
            return Response.status(Status.FORBIDDEN).build();
        }
    }

    @GET
    @Authenticated
    @Operation(
        operationId = "getMe",
        summary = "Get current user information"
    )
    @Path("/me")
    public RestResponse<AuthMeResponse> me(@Context SecurityContext securityContext) {
        try {
            return RestResponse.ok(
                new AuthMeResponse(
                    (JsonWebToken) securityContext.getUserPrincipal(),
                    userService.fromPrincipal(securityContext.getUserPrincipal())
                )
            );
        } catch (IllegalArgumentException e) {
            return RestResponse.status(Status.UNAUTHORIZED);
        } catch (IllegalStateException e) {
            return RestResponse.status(Status.FORBIDDEN);
        }
    }

    @POST
    @Path("/consume")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "postConsume",
        summary = "Retrieve the user associated with this token, optionally creating an account.",
        description = "Consumes a Hanko token, and converts it into a valid session."
    )
    public AuthConsumeResponse postConsume(@RequestBody @Valid AuthConsumeRequest request) {
        try {
            var user = authenticationService.consume(request.token());

            return AuthConsumeResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .jwt(request.token())
                .build();
        } catch (AuthenticationService.AuthenticationException exception) {
            log.warn("Failed to login: {} {}", exception.getReason(), exception.getMessage());

            throw switch (exception.getReason()) {
                case SystemFailure -> new InternalServerErrorException();
                case AccountLocked -> new ForbiddenException();
                default -> new UnauthorizedException();
            };
        }
    }
}
