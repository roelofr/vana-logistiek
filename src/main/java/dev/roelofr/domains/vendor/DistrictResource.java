package dev.roelofr.domains.vendor;

import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.vendor.model.District;
import dev.roelofr.domains.vendor.service.DistrictService;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Authenticated
@Path("/districts")
@RequiredArgsConstructor
@Tags({@Tag(name = "Vendors")})
public class DistrictResource {
    private final DistrictService districtService;
    private final VendorResource vendorResource;
    private final VendorService vendorService;
    private final UserService userService;

    @GET
    @Transactional
    @Operation(operationId = "districtList", summary = "Lists all districts")
    public RestResponse<List<District>> getIndex() {
        return RestResponse.ok(
            districtService.getAllSorted()
        );
    }
}
