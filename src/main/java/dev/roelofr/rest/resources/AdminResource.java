package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.rest.request.ActivateUserRequest;
import dev.roelofr.service.UserService;
import dev.roelofr.service.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.File;
import java.util.List;

@Slf4j
@Path("/admin")
@RolesAllowed(Roles.Admin)
public class AdminResource {
    private final VendorService vendorService;
    private final UserService userService;

    @Inject
    public AdminResource(VendorService vendorService, UserService userService) {
        this.vendorService = vendorService;
        this.userService = userService;
    }

    @POST
    @Path("/import-vendors")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public RestResponse<List<Vendor>> importVendorList(File file) {
        try {
            var vendors = vendorService.importVendorList(file);
            return RestResponse.ok(vendors);
        } catch (WebApplicationException e) {
            log.warn("Web request failed: {} {}", e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    @POST
    @Path("/users/{id}/activate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<User> activateUser(@Context SecurityContext context, @PathParam("id") Long userId, @Valid ActivateUserRequest request) {
        log.info("User {} wants to activate user {} with roles {} and district {}", context.getUserPrincipal().getName(), userId, request.roles(), request.district());

        var user = userService.activateUser(context.getUserPrincipal(), userId, request.roles(), request.district());

        return RestResponse.ok(user);
    }
}
