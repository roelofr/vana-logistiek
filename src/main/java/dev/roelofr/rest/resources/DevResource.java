package dev.roelofr.rest.resources;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import java.net.URI;
import java.net.URISyntaxException;

@Path("/dev")
@IfBuildProfile("dev")
public class DevResource {
    @ConfigProperty(name = "quarkus.http.port")
    int port;

    private RestResponse<String> response(Status status, String message) {
        return RestResponse.ResponseBuilder.ok(message)
            .status(status)
            .build();
    }

    @Path("/token")
    @Authenticated
    public RestResponse<String> redirectToken(@QueryParam("next") String next, @Context SecurityContext securityContext) {
        if (next == null || next.isBlank())
            return response(Status.BAD_REQUEST, "Next location is missing!");

        var principal = securityContext.getUserPrincipal();
        if (!(principal instanceof JsonWebToken jwtPrincipal))
            return response(Status.PRECONDITION_FAILED, "Login session is not a JWT!");


        try {
            var baseUrl = URI.create(String.format("http://localhost:%d", port));
            var nextUrl = new URI(next);

            var redirectUrl = UriBuilder.fromUri(baseUrl.resolve(nextUrl))
                .queryParam("token", jwtPrincipal.getRawToken())
                .build();

            return RestResponse.temporaryRedirect(redirectUrl);
        } catch (URISyntaxException e) {
            return response(Status.BAD_REQUEST, "Next location could not be parsed!");
        }
    }
}
