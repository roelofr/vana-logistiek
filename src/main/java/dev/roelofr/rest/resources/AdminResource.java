package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.Vendor;
import dev.roelofr.service.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.File;
import java.util.List;

@Slf4j
@Path("/admin")
@RolesAllowed(Roles.Admin)
public class AdminResource {
    private final VendorService vendorService;

    @Inject
    public AdminResource(VendorService vendorService) {
        this.vendorService = vendorService;
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
}
