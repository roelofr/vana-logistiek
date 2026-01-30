package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.Vendor;
import dev.roelofr.rest.dtos.ThreadHttpDto;
import dev.roelofr.rest.request.VendorUpdateRequest;
import dev.roelofr.service.DistrictService;
import dev.roelofr.service.UserService;
import dev.roelofr.service.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Path("/vendors")
@RequiredArgsConstructor()
@Tag(name = "Vendors")
@RolesAllowed(Roles.User)
public class VendorResource {
    private final VendorService vendorService;
    private final UserService userService;
    private final DistrictService districtService;

    @GET
    @Path("/")
    @Operation(operationId = "getVendorList")
    public List<Vendor> getVendorList(@Context SecurityContext context) {
        var user = userService.fromPrincipal(context.getUserPrincipal());

        return vendorService.listVendors(user);
    }

    @GET
    @Path("/{number}")
    @Operation(operationId = "getVendor")
    public RestResponse<Vendor> getVendor(@PathParam("number") String number) {
        return vendorService.getVendor(number)
            .map(RestResponse::ok)
            .orElse(RestResponse.notFound());
    }

    @POST
    @Path("/{number}")
    @Operation(operationId = "updateVendor")
    public RestResponse<Vendor> updateVendor(@PathParam("number") String number, VendorUpdateRequest request) {
        var vendor = vendorService.getVendor(number).orElse(null);
        if (vendor == null)
            return RestResponse.notFound();

        if (request.district() != null) {
            var district = districtService.findByNameOptional(request.district()).orElse(null);
            if (district == null)
                throw new BadRequestException("District %s was not found".formatted(request.district()));

            vendor.setDistrict(district);
        }

        if (request.name() != null)
            vendor.setName(request.name());

        if (request.type() != null)
            vendor.setVendorType(request.type());

        return RestResponse.status(RestResponse.Status.RESET_CONTENT, vendor);
    }

    @GET
    @Path("/{number}/tickets")
    @Operation(operationId = "getVendorThreads")
    public RestResponse<List<ThreadHttpDto>> getVendorThreads(@PathParam("number") String number) {
        var vendor = vendorService.getVendor(number).orElse(null);
        if (vendor == null)
            return RestResponse.notFound();

        return RestResponse.ok(
            List.of() // TODO
        );
    }
}
