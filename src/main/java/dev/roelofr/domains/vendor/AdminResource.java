package dev.roelofr.domains.vendor;

import dev.roelofr.config.Roles;
import dev.roelofr.domains.vendor.dto.CreateVendorRequest;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.service.DistrictService;
import dev.roelofr.domains.vendor.service.VendorAdminService;
import dev.roelofr.domains.vendor.service.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.File;
import java.util.List;

@Slf4j
@Path("/vendors/admin")
@AllArgsConstructor
@RolesAllowed(Roles.Admin)
@Tags({@Tag(name = "Admin"), @Tag(name = "Vendors")})
public class AdminResource {
    private final VendorAdminService vendorAdminService;
    private final DistrictService districtService;
    private final VendorService vendorService;

    @POST
    @Transactional
    @RolesAllowed(Roles.Admin)
    @Operation(operationId = "adminVendorCreate", summary = "Create a single, missing vendor")
    public RestResponse<Vendor> createVendor(@Valid CreateVendorRequest request) {
        final var district = districtService.findById(request.districtId()).orElse(null);
        if (district == null)
            return RestResponse.notFound();

        var createdVendor = vendorService.createVendor(district, request.number(), request.name());
        return RestResponse.ok(createdVendor);
    }

    @POST
    @Path("/import")
    @Operation(operationId = "vendorAdminImport", summary = "Import a list of vendors")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public RestResponse<List<Vendor>> importVendorList(@FormParam("file") File file) {
        try {
            var vendors = vendorAdminService.importVendorList(file);
            return RestResponse.ok(vendors);
        } catch (WebApplicationException e) {
            log.info("Failed to import Excel file: {}", e.getMessage());
            throw e;
        }
    }
}
