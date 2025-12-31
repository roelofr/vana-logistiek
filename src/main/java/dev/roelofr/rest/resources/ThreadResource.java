package dev.roelofr.rest.resources;

import dev.roelofr.Roles;
import dev.roelofr.domain.Thread;
import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.enums.UpdateType;
import dev.roelofr.repository.ThreadRepository;
import dev.roelofr.repository.ThreadUpdateRepository;
import dev.roelofr.rest.dtos.ThreadMessage;
import dev.roelofr.rest.mappers.ThreadMessageMapper;
import dev.roelofr.rest.request.ThreadCommentRequest;
import dev.roelofr.rest.request.ThreadCreateRequest;
import dev.roelofr.service.ThreadService;
import dev.roelofr.service.UserService;
import dev.roelofr.service.VendorService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
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
    private final ThreadUpdateRepository threadUpdateRepository;
    private final ThreadMessageMapper threadMessageMapper;
    private final UserService userService;

    @Context
    SecurityIdentity securityIdentity;

    @GET
    public RestResponse<List<Thread>> listThreads(
        @RestQuery("closed") boolean includeClosed
    ) {
        if (includeClosed)
            return RestResponse.ok(
                threadRepository.listAllSorted()
            );

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
        threadService.createUpdate(thread, UpdateType.Created);

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

    @GET
    @Path("/{id}/updates")
    @Transactional
    public RestResponse<List<ThreadMessage>> showThreadUpdates(@Positive @PathParam("id") Long id) {
        var thread = threadRepository.findById(id);
        if (thread == null) {
            log.info("Failed to find thread with ID {}", id);
            return RestResponse.notFound();
        }

        var updates = threadUpdateRepository.findByThread(thread);
        var user = userService.findBySecurityIdentity(securityIdentity).orElse(null);

        return RestResponse.ok(
            threadMessageMapper.mapUpdatesToMessages(updates, user)
        );
    }

    @POST
    @Transactional
    @Path("/{id}/message")
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Void> addThreadMessage(@Positive @PathParam("id") Long id, @Valid ThreadCommentRequest body) {
        var thread = threadRepository.findById(id);
        if (thread == null)
            return RestResponse.notFound();

        var update = (ThreadUpdate.ThreadMessage) threadService.createUpdate(thread, UpdateType.Message);
        update.setMessage(body.message());

        return RestResponse.status(Status.RESET_CONTENT);
    }
}
