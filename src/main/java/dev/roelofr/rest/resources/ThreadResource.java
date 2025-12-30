package dev.roelofr.rest.resources;

import dev.roelofr.Roles;
import dev.roelofr.domain.Thread;
import dev.roelofr.repository.ThreadRepository;
import dev.roelofr.rest.request.ThreadCreateRequest;
import dev.roelofr.service.ThreadService;
import dev.roelofr.service.VendorService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import java.util.List;

@Slf4j
@Authenticated
@Path("/threads")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class ThreadResource {
    final private ThreadService threadService;
    final private ThreadRepository threadRepository;
    private final VendorService vendorService;

    @Context
    SecurityIdentity securityIdentity;

    String activeUser() {
        var p = securityIdentity.getPrincipal();
        log.info("P = {}", p.getClass().getName());
        log.info("Name = {}", p.getName());

        return p.getName();
    }

    @GET
    public RestResponse<List<Thread>> listThreads(
        @RestQuery("closed") boolean includeClosed
    ) {
        if (includeClosed) {
            log.info("User {} requested all threads", activeUser());
            return RestResponse.ok(
                threadRepository.listAllSorted()
            );
        }

        log.info("User {} requested active threads", activeUser());

        return RestResponse.ok(
            threadRepository.listUnresolvedSorted()
        );
    }

    @POST
    @RolesAllowed({
        Roles.Admin,
        Roles.Wijkhouder,
        Roles.CentralePost
    })
    @Transactional
    public RestResponse<Thread> createThread(@Valid ThreadCreateRequest request) {
        var vendor = vendorService.getVendor(request.vendorId());
        if (vendor == null)
            throw new BadRequestException("Vendor could not be found");

        var thread = threadService.createThread(vendor, request.subject());

        return RestResponse.status(Status.CREATED, thread);
    }

    @GET
    @Path("/{id}")
    @Transactional
    public RestResponse<Thread> showThread(@Positive @PathParam("id") Long id) {
        var threadOptional = threadRepository.findByIdWithAllRelations(id);

        if (threadOptional.isPresent())
            return RestResponse.ok(threadOptional.get());

        return RestResponse.notFound();
    }

}
