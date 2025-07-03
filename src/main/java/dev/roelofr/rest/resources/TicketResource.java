package dev.roelofr.rest.resources;

import dev.roelofr.domain.enums.TicketType;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.TicketRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.repository.VendorRepository;
import dev.roelofr.rest.dtos.TicketHttpDto;
import dev.roelofr.rest.dtos.TicketListHttpDto;
import dev.roelofr.rest.request.TicketCreateRequest;
import dev.roelofr.service.AuthenticationService;
import dev.roelofr.service.TicketService;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Slf4j
@Authenticated
@Path("/ticket")
@RequiredArgsConstructor
@Tag(name = "Tickets")
public class TicketResource {
    private final TicketRepository ticketRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final AuthenticationService authenticationService;
    private final TicketService ticketService;

    /**
     * Lists all tickets
     */
    @GET
    @Operation(operationId = "getTicketList", description = "Lists all tickets")
    public List<TicketListHttpDto> list() {
        return ticketRepository.streamAll().map(TicketListHttpDto::new).toList();
    }

    @GET
    @Path("/by-district/{district}")
    @Operation(operationId = "getTicketListByDistrict", description = "Lists all tickets for a given district")
    public List<TicketListHttpDto> listForDistrict(@PathParam("district") @Parameter(description = "Name of the district") String districtName) {
        var districtOptional = districtRepository.findByName(districtName);
        if (districtOptional.isEmpty())
            throw new NotFoundException("District with name [%s] not found!".formatted(districtName));

        return ticketRepository.findForDistrict(districtOptional.get())
            .stream()
            .map(TicketListHttpDto::new)
            .toList();
    }

    @GET
    @Path("/by-user/me")
    @Operation(operationId = "getTicketListByCurrentUser", description = "Lists all tickets for the current user")
    public List<TicketListHttpDto> listForUser() {
        var userOptional = authenticationService.getCurrentUser();
        if (userOptional.isEmpty())
            throw new InternalServerErrorException("Failed to resolve current user.");

        return ticketRepository.findForUser(userOptional.get())
            .stream()
            .map(TicketListHttpDto::new)
            .toList();
    }

    /**
     * Finds the ticket with the given ID.
     * Throws a 404 if not found.
     */
    @GET
    @Path("/{id}")
    @Operation(operationId = "getTicketById", description = "Find a ticket with a given ID")
    public TicketHttpDto find(@PathParam("id") @Parameter(description = "ID of the ticket") long id) {
        var ticketOpt = ticketRepository.findByIdOptional(id);
        if (ticketOpt.isEmpty()) throw new NotFoundException("Ticket with id %d could not be found!".formatted(id));

        return new TicketHttpDto(ticketOpt.get());
    }

    /**
     * Creates a new ticket, assigned to the current logged-in user.
     */
    @POST
    @Operation(operationId = "postTicketCreate", description = "Makes a new ticket with the given description.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Ticket was created ok and has been assigned an ID."),
        @APIResponse(responseCode = "404", description = "Vendor was not found"),
        @APIResponse(responseCode = "500", description = "User could not be tied to ticket")
    })
    @Transactional
    public Response create(@RequestBody TicketCreateRequest body) {
        log.info("Got new ticket request [{}]", body);

        var vendorOptional = vendorRepository.findByIdOptional(body.vendorId());

        if (vendorOptional.isEmpty()) {
            log.warn("Failed to find vendor with ID [{}]", body.vendorId());
            throw new NotFoundException("Vendor with id %d could not be found!".formatted(body.vendorId()));
        }

        var vendor = vendorOptional.get();

        var user = authenticationService.getCurrentUser()
            .orElseThrow(() -> new InternalServerErrorException("No current user available"));

        var ticket = ticketService.createTicket(user, vendor, TicketType.Generic, body.description());

        return Response.status(Status.CREATED).entity(new TicketHttpDto(ticket)).build();
    }
}
