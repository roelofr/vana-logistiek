package dev.roelofr.domains.vendor;

import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.service.DistrictService;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Authenticated
@Path("/vendors")
@RequiredArgsConstructor
@Tags({@Tag(name = "Vendors")})
public class VendorResource {
    private final VendorService vendorService;
    private final DistrictService districtService;

    @GET
    @Transactional
    @Operation(operationId = "vendorList", summary = "Lists all vendors")
    public RestResponse<List<Vendor>> getVendors() {
        return RestResponse.ok(
            vendorService.listVendors()
        );
    }
}
