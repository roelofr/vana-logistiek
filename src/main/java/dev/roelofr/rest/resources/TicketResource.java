package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.Ticket;
import dev.roelofr.rest.request.TicketCreateRequest;
import dev.roelofr.service.TicketService;
import dev.roelofr.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Path("/ticket")
@Tag(name = "Tickets")
@RequiredArgsConstructor
@RolesAllowed(Roles.User)
public class TicketResource {
    public final TicketService ticketService;
    private final UserService userService;

    @GET
    public RestResponse<List<Ticket>> list() {
        return RestResponse.ok(ticketService.list());
    }

    @GET
    @Path("/by-district/{district}")
    @Operation(operationId = "getTicketListByDistrict", description = "Lists all tickets for a given district")
    public RestResponse<List<Ticket>> listByDistrictName(@PathParam("district") @Parameter(description = "Name of the district") String districtName) {
        try {
            return RestResponse.ok(ticketService.listByDistrictName(districtName));
        } catch (IllegalArgumentException e) {
            return RestResponse.notFound();
        }
    }

    @GET
    @Path("/by-user/me")
    @Operation(operationId = "getTicketListByCurrentUser", description = "Lists all tickets for the current user")
    public RestResponse<List<Ticket>> listForUser(@Context SecurityContext securityContext) {
        var user = userService.fromPrincipal(securityContext.getUserPrincipal());
        assert user != null;

        return RestResponse.ok(ticketService.listByUser(user));
    }

    @GET
    @Path("/{id}")
    @Operation(operationId = "getTicket", description = "Finds a single ticket by ID")
    public RestResponse<Ticket> getTicketById(@PathParam("id") @Parameter(description = "ID of the ticket") Long id) {
        var ticket = ticketService.findById(id);
        if (ticket.isEmpty())
            return RestResponse.notFound();

        return RestResponse.ok(ticket.get());
    }

    @POST
    public RestResponse<Ticket> create(@Valid TicketCreateRequest request) {
        try {
            var result = ticketService.createFromRequest(request);
            return RestResponse.ok(result);
        } catch (IllegalArgumentException exception) {
            log.warn("Failed to create ticket: {}", exception.getMessage(), exception);
            throw exception;
        }
    }
}
