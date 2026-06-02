package dev.roelofr.domains.vendor;

import dev.roelofr.config.Roles;
import dev.roelofr.domains.vendor.dto.CreateVendorRequest;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.service.DistrictService;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Authenticated
@Path("/vendors")
@RequiredArgsConstructor
public class VendorResource {
    private final VendorService vendorService;
    private final DistrictService districtService;

    @GET
    @Transactional
    public RestResponse<List<Vendor>> getVendors() {
        return RestResponse.ok(
            vendorService.listVendors()
        );
    }

    @POST
    @Transactional
    @RolesAllowed(Roles.Admin)
    public RestResponse<Vendor> createVendor(@Valid CreateVendorRequest request) {
        final var district = districtService.findById(request.districtId()).orElse(null);
        if (district == null)
            return RestResponse.notFound();

        var createdVendor = vendorService.createVendor(district, request.name(), request.number());
        return RestResponse.ok(createdVendor);
    }
}
