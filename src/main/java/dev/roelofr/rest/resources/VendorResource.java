package dev.roelofr.rest.resources;

import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.VendorRepository;
import dev.roelofr.rest.dtos.TicketListHttpDto;
import dev.roelofr.rest.dtos.VendorHttpDto;
import dev.roelofr.rest.request.VendorCreateRequest;
import dev.roelofr.service.TicketService;
import dev.roelofr.service.VendorService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

import static jakarta.ws.rs.core.Response.Status;

@Slf4j
@Path("/vendor")
@RequiredArgsConstructor()
@Tag(name = "Vendors")
@Produces(MediaType.APPLICATION_JSON)
public class VendorResource {
    final VendorService vendorService;
    final VendorRepository vendorRepository;
    private final TicketService ticketService;

    @GET
    @Path("/")
    @Operation(operationId = "VendorList")
    public List<Vendor> listVendor() {
        return vendorRepository.listAllSorted();
    }

    @POST
    @Path("/")
    @Operation(operationId = "VendorCreate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createVendor(VendorCreateRequest request) {
        var vendor = vendorService.createVendor(
            request.district(),
            request.number(),
            request.name()
        );

        return Response.status(Status.CREATED)
            .entity(new VendorHttpDto(vendor))
            .build();
    }

    @GET
    @Path("/{id}")
    @Operation(operationId = "VendorGet")
    public Vendor getVendor(@PathParam("id") Long id) {
        return Optional.ofNullable(vendorService.getVendor(id))
            .orElseThrow(() -> new NotFoundException("User with id %d not found".formatted(id)));
    }

    @GET
    @Path("/{id}/tickets")
    @Operation(operationId = "VendorGetTickets")
    public Response getVendorTickets(@PathParam("id") Long id) {
        var vendor = vendorService.getVendor(id);
        if (vendor == null)
            throw new NotFoundException("User with id %d not found".formatted(id));

        var tickets = ticketService.findByVendor(vendor);

        return Response.ok(
            tickets.stream()
                .map(TicketListHttpDto::new)
                .toList()
        ).build();
    }
}
