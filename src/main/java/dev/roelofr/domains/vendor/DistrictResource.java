package dev.roelofr.domains.vendor;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Authenticated
@Path("/districts")
@RequiredArgsConstructor
public class DistrictResource {
    private final DistrictService districtService;

    @GET
    @Transactional
    public RestResponse<List<District>> getAll() {
        return RestResponse.ok(
            districtService.getAllSorted()
        );
    }

}
