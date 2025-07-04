package dev.roelofr.rest.resources;

import dev.roelofr.domain.User;
import dev.roelofr.domain.rest.PostLoginRequest;
import dev.roelofr.domain.rest.PostLoginResponse;
import dev.roelofr.domain.rest.PostRegisterRequest;
import dev.roelofr.service.AuthenticationService;
import dev.roelofr.service.AuthenticationService.ActingUser;
import io.quarkiverse.bucket4j.runtime.RateLimited;
import io.quarkiverse.bucket4j.runtime.resolver.IpResolver;
import io.quarkus.security.Authenticated;
import io.quarkus.security.UnauthorizedException;
import io.vertx.core.http.HttpServerRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

@Slf4j
@Path("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationResource {
    private final AuthenticationService authenticationService;

    @GET
    @Authenticated
    @Operation(
        operationId = "getMe",
        summary = "Get current user information"
    )
    @Path("/me")
    public RestResponse<User> me() {
        var me = authenticationService.getCurrentUser();

        if (me.isPresent())
            return RestResponse.ok(me.get());

        throw new ForbiddenException("User not found");
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "postLogin",
        summary = "Log in with username and password",
        description = "Starts a new user session by attempting to login with the username and password. Rate limited."
    )
    @RateLimited(bucket = "authentication", identityResolver = IpResolver.class)
    public PostLoginResponse postLogin(
        @Context HttpServerRequest request,
        @RequestBody @Valid PostLoginRequest postLoginRequest
    ) {
        var result = authenticationService.authenticate(
            ActingUser.fromRequest(request),
            postLoginRequest.username(),
            postLoginRequest.password()
        );

        if (!result.success()) {
            log.info("Login for {} failed: {}", postLoginRequest.username(), result.reason());

            throw switch (result.reason()) {
                case SystemFailure -> new InternalServerErrorException();
                case AccountLocked -> new ForbiddenException();
                default -> new UnauthorizedException();
            };
        }

        return PostLoginResponse.builder()
            .name(result.user().getName())
            .email(result.user().getEmail())
            .roles(result.user().getRoles())
            .jwt(result.token())
            .expiration(result.tokenExpiration())
            .build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "postRegister",
        summary = "Creates a new, deactivated user",
        description = "Creates a new user with the given e-mail, name and password. Rate limited."
    )
    @RateLimited(bucket = "authentication", identityResolver = IpResolver.class)
    public Response postRegister(
        @Context HttpServerRequest httpRequest,
        @RequestBody @Valid PostRegisterRequest request
    ) {
        if (!request.acceptTerms()) {
            throw new BadRequestException("De voorwaarden moeten akkoord zijn.");
        }

        var result = authenticationService.register(
            ActingUser.fromRequest(httpRequest),
            request.email(),
            request.password(),
            request.name()
        );

        if (result.success()) {
            log.info("Register of user {} OK", request.email());
            return Response.created(null).build();
        }

        log.info("Register for {} failed: {}", request.email(), result.reason());

        throw switch (result.reason()) {
            case SystemFailure -> new InternalServerErrorException(result.reason().getMessage());
            case AccountAlreadyExists -> new ClientErrorException(result.reason().getMessage(), Status.CONFLICT);
            default -> new BadRequestException(result.reason().getMessage());
        };
    }
}
