package dev.roelofr.domains.vendor;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
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

    @GET
    @Path("/")
    public RestResponse<List<Vendor>> getVendors() {
        return RestResponse.ok(
            vendorService.listVendors()
        );
    }
}
