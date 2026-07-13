package dev.roelofr.domains.users;

import dev.roelofr.config.AppConfig;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Authenticated
@Path("/roles")
@ApplicationScoped
@RequiredArgsConstructor
public class RoleResource {
    private final AppConfig config;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "rolesGet",
        summary = "Get all roles"
    )
    public RestResponse<List<AppRoleResponse>> getRoles() {
        return RestResponse.ok(
            List.of(
                new AppRoleResponse("Gebruiker", config.roles().user()),
                new AppRoleResponse("Wijkhouder", config.roles().wijkhouder()),
                new AppRoleResponse("Centrale Post", config.roles().centralePost()),
                new AppRoleResponse("Admin", config.roles().admin())
            )
        );
    }

    public record AppRoleResponse(String label, String value) {

    }
}
