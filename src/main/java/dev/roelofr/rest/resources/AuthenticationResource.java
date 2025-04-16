package dev.roelofr.rest.resources;

import dev.roelofr.openapi.GetMeResponse;
import dev.roelofr.openapi.PostLoginRequest;
import dev.roelofr.openapi.PostLoginResponse;
import io.quarkiverse.bucket4j.runtime.RateLimited;
import io.quarkiverse.bucket4j.runtime.resolver.IpResolver;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/auth")
public class AuthenticationResource {
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
    public PostLoginResponse postLogin(PostLoginRequest postLoginRequest) {
        return null;
    }
}
