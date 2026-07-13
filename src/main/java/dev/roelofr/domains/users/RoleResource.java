package dev.roelofr.domains.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.roelofr.Roles;
import dev.roelofr.config.AppConfig;
import dev.roelofr.domains.users.model.User;
import io.quarkus.runtime.util.StringUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Collection;
import java.util.List;

@Authenticated
@Path("/roles")
@ApplicationScoped
public class RoleResource {
    private final List<AppRoleResponse> roles;

    @Inject
    public RoleResource(AppConfig config) {
        roles = List.of(
            new AppRoleResponse(config.roles().user(), "Gebruiker"),
            new AppRoleResponse(config.roles().wijkhouder(), "Wijkhouder"),
            new AppRoleResponse(config.roles().centralePost(), "Centrale Post"),
            new AppRoleResponse(config.roles().admin(), "Admin")
        );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "rolesGet",
        summary = "Get all roles"
    )
    @RolesAllowed({Roles.Admin})
    public RestResponse<Collection<AppRoleResponse>> getRoles() {
        return RestResponse.ok(roles);
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "rolesGetMe",
        summary = "List your own roles, as provided by the system. May not match JWT."
    )
    public RestResponse<Collection<String>> getMyRoles(@Context User user) {
        return RestResponse.ok(
            roles.stream()
                .filter(role -> user.getRoles().contains(role.role))
                .map(AppRoleResponse::alias)
                .toList()
        );
    }

    public record AppRoleResponse(@JsonProperty("value") String role, String label, String alias) {
        public AppRoleResponse(String role, String label) {
            this(role, label, StringUtil.hyphenate(label));
        }
    }
}
