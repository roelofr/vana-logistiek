package dev.roelofr.rest.resources;

import dev.roelofr.domain.Ticket;
import dev.roelofr.rest.dtos.TicketListHttpDto;
import dev.roelofr.rest.request.TicketCreateRequest;
import dev.roelofr.service.TicketService;
import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import java.util.List;

@Slf4j
@Authenticated
@Path("/ticket")
@Tag(name = "Tickets")
@RequiredArgsConstructor
public class TicketResource {
    public final TicketService ticketService;

    @GET
    public RestResponse<List<Ticket>> list() {
        return ResponseBuilder.ok(ticketService.list()).build();
    }

    @GET
    @Path("/by-district/{district}")
    @Operation(operationId = "getTicketListByDistrict", description = "Lists all tickets for a given district")
    public List<Ticket> listByDistrictName(@PathParam("district") @Parameter(description = "Name of the district") String districtName) {
        return List.of();
    }

    @GET
    @Path("/by-user/me")
    @Operation(operationId = "getTicketListByCurrentUser", description = "Lists all tickets for the current user")
    public List<TicketListHttpDto> listForUser() {
        return List.of();
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
