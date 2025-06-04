package dev.roelofr.rest.resources;

import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.VendorRepository;
import dev.roelofr.rest.dtos.DistrictHttpDto;
import dev.roelofr.rest.dtos.VendorHttpDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/districts")
@RequiredArgsConstructor
@Tag(name = "Districts")
public class DistrictResource {
    private final DistrictRepository districtRepository;
    private final VendorRepository vendorRepository;

    @GET
    public List<DistrictHttpDto> index() {
        return districtRepository.streamAll()
            .map(DistrictHttpDto::new)
            .toList();
    }

    @GET
    @Path("/{district}/vendor")
    public List<VendorHttpDto> listVendors(@PathParam("district") String districtName) {
        var district = districtRepository.findByName(districtName).orElseThrow(NotFoundException::new);

        return vendorRepository.stream("district", district)
            .sorted()
            .map(VendorHttpDto::new)
            .toList();
    }
}
