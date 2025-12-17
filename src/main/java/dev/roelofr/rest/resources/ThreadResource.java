package dev.roelofr.rest.resources;

import dev.roelofr.domain.Thread;
import dev.roelofr.repository.ThreadRepository;
import dev.roelofr.rest.request.ThreadCreateRequest;
import dev.roelofr.service.ThreadService;
import dev.roelofr.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import java.util.List;

@Authenticated
@Path("/threads")
@RequiredArgsConstructor
public class ThreadResource {
    final private ThreadService threadService;
    final private ThreadRepository threadRepository;
    private final VendorService vendorService;

    @GET
    public RestResponse<List<Thread>> listThreads(
        @QueryParam("closed") Boolean includeClosed
    ) {
        if (includeClosed == true)
            return RestResponse.ok(
                threadRepository.listAllSorted()
            );

        return RestResponse.ok(
            threadRepository.listUnresolvedSorted()
        );
    }

    @POST
    @Transactional
    public RestResponse<Thread> createThread(@Valid ThreadCreateRequest request) {
        var vendor = vendorService.getVendor(request.vendorId());
        if (vendor == null)
            throw new BadRequestException("Vendor could not be found");

        var thread = threadService.createThread(vendor, request.title());

        return RestResponse.status(Status.CREATED, thread);
    }

    @GET
    @Path("/{id}")
    @Transactional
    public RestResponse<Thread> findThread(@Positive @PathParam("id") Long id) {
        var threadOptional = threadRepository.findByIdWithAllRelations(id);

        if (threadOptional.isPresent())
            return RestResponse.ok(threadOptional.get());

        return RestResponse.notFound();
    }

}
