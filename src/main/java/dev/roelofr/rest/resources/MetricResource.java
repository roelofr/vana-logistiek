package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.repository.TicketMetricRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.Duration;
import java.util.List;

@Path("/metrics")
@RolesAllowed(Roles.User)
public class MetricResource {
    private final TicketMetricRepository ticketMetricRepository;

    @Inject
    public MetricResource(TicketMetricRepository ticketMetricRepository) {
        this.ticketMetricRepository = ticketMetricRepository;
    }

    @GET
    @Path("/tickets")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<List<TicketMetric>> getTicketMetrics() {
        var tickets = ticketMetricRepository.findSince(Duration.ofHours(48));

        return RestResponse.ok(tickets);
    }
}
