package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.Vendor;
import dev.roelofr.rest.dtos.ThreadHttpDto;
import dev.roelofr.service.UserService;
import dev.roelofr.service.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.Optional;

@Slf4j
@Path("/vendors")
@RequiredArgsConstructor()
@Tag(name = "Vendors")
@RolesAllowed(Roles.User)
public class VendorResource {
    private final VendorService vendorService;
    private final UserService userService;

    @GET
    @Path("/")
    @Operation(operationId = "getVendorList")
    public List<Vendor> getVendorList(@Context SecurityContext context) {
        var user = userService.fromPrincipal(context.getUserPrincipal());

        return vendorService.listVendors(user);
    }

    @GET
    @Path("/{id}")
    @Operation(operationId = "getVendor")
    public Vendor getVendor(@PathParam("id") Long id) {
        return Optional.ofNullable(vendorService.getVendor(id))
            .orElseThrow(() -> new NotFoundException("User with id %d not found".formatted(id)));
    }

    @GET
    @Path("/{id}/tickets")
    @Operation(operationId = "getVendorThreads")
    public RestResponse<List<ThreadHttpDto>> getVendorThreads(@PathParam("id") Long id) {
        var vendor = vendorService.getVendor(id);
        if (vendor == null)
            throw new NotFoundException("User with id %d not found".formatted(id));

        return RestResponse.ok(
            List.of() // TODO
        );
    }
}
