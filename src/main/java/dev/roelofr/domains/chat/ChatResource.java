package dev.roelofr.domains.chat;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

@Path("/chats")
@Authenticated
public class ChatResource {
    @Context
    SecurityContext securityContext;

    @Context
    JsonWebToken jwt;

    @GET
    @Path("/")
    public RestResponse<Map<String, Object>> index() {
        return RestResponse.ok(
            Map.ofEntries(
                Map.entry("auth-secure", securityContext.isSecure()),
                Map.entry("auth-scheme", securityContext.getAuthenticationScheme()),
                Map.entry("auth-principal", securityContext.getUserPrincipal()),
                Map.entry("claims", jwt.getClaimNames()),
                Map.entry("audiences", jwt.getAudience()),
                Map.entry("groups", jwt.getGroups())
            )
        );
    }
}
