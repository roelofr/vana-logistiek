package dev.roelofr.domains.vendor;

import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.vendor.dto.DistrictDto;
import dev.roelofr.domains.vendor.model.District;
import dev.roelofr.domains.vendor.service.DistrictService;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MultivaluedHashMap;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.ArrayList;
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

    @GET
    @Path("/summary")
    @Transactional
    @Operation(operationId = "districtSummaryList", summary = "Lists all districts, for admin viewing")
    public RestResponse<List<DistrictDto>> getSummary() {
        var districts = districtService.getAllSorted();

        var mappedUsers = new MultivaluedHashMap<Group, User>();
        userService.listAllWithGroups()
            .stream()
            .filter(user -> !user.getGroups().isEmpty())
            .forEach(user -> mappedUsers.add(user.getGroup(), user));

        var districtDtos = new ArrayList<DistrictDto>();

        for (var district : districts) {
            var vendors = vendorService.listInDistrict(district);

            var group = district.getGroup();
            var users = mappedUsers.getOrDefault(group, List.of());

            districtDtos.add(
                new DistrictDto(district, group, vendors, users)
            );
        }

        return RestResponse.ok(districtDtos);
    }
}
