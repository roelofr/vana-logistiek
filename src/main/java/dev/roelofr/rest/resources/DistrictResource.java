package dev.roelofr.rest.resources;

import dev.roelofr.domain.District;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.rest.dtos.DistrictHttpDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("/districts")
@RequiredArgsConstructor
public class DistrictResource {
    private final DistrictRepository districtRepository;

    @GET
    public List<DistrictHttpDto> index() {
        return districtRepository.streamAll()
            .map(this::toDto)
            .toList();
    }

    private DistrictHttpDto toDto(District district) {
        return new DistrictHttpDto(district);

    }

}
