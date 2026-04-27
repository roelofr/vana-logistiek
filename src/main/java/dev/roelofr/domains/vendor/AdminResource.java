package dev.roelofr.domains.vendor;

import dev.roelofr.config.Roles;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.service.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.File;
import java.util.List;

@Slf4j
@Path("/vendors/admin")
@AllArgsConstructor
@RolesAllowed(Roles.Admin)
public class AdminResource {
    private final VendorService vendorService;

    @POST
    @Path("/import")
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
}
