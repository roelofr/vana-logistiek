package dev.roelofr.rest.resources;

import dev.roelofr.Roles;
import dev.roelofr.domain.Thread;
import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.enums.UpdateType;
import dev.roelofr.domain.projections.ListThread;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;
import org.jboss.resteasy.reactive.RestResponse.Status;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@Authenticated
@Path("/threads")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class ThreadResource {
    private static final MediaType MEDIATYPE_WEBP = MediaType.valueOf("image/webp");
    private static final CacheControl CACHE_CONTROL_LONG_BUT_PRIVATE;

    static {
        CACHE_CONTROL_LONG_BUT_PRIVATE = new CacheControl();
        CACHE_CONTROL_LONG_BUT_PRIVATE.setPrivate(true);
        CACHE_CONTROL_LONG_BUT_PRIVATE.setMaxAge((int) Duration.ofDays(300).toSeconds());
    }

    private final ThreadService threadService;
    private final ThreadRepository threadRepository;
    private final VendorService vendorService;
    private final ThreadUpdateRepository threadUpdateRepository;
    private final ThreadMessageMapper threadMessageMapper;
    private final UserService userService;

    @Context
    SecurityIdentity securityIdentity;

    @GET
    public RestResponse<List<ListThread>> listThreads(
        @RestQuery("closed") boolean includeClosed
    ) {
        return RestResponse.ok(
            threadService.findAll(includeClosed)
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
        var vendor = vendorService.getVendor(request.vendorId()).orElse(null);
        if (vendor == null)
            throw new BadRequestException("Vendor could not be found");

        var thread = threadService.createThread(vendor, request.subject());
        threadService.createUpdate(thread, UpdateType.Created);

        if (request.message() != null) {
            var update = (ThreadUpdate.ThreadMessage) threadService.createUpdate(thread, UpdateType.Message);
            update.setMessage(request.message());
        }

        return RestResponse.status(Status.CREATED, thread);
    }

    @GET
    @Path("/{id}")
    @Transactional
    public RestResponse<Thread> showThread(@Positive @PathParam("id") Long id) {
        var threadOptional = threadRepository.findByIdWithAllRelations(id);

        log.info("Search for thread {} resulted in: {}", id, threadOptional);

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

    @GET
    @Path("/{id}/image/{updateid}/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public RestResponse<java.nio.file.Path> showThreadAttachmentImage(
        @Positive @PathParam("id") Long id,
        @Positive @PathParam("updateid") Long updateId,
        @NotBlank @PathParam("filename") String filename
    ) throws IOException {
        var update = threadUpdateRepository.findById(updateId);
        if (update == null) {
            log.info("Attachment lookup {} failed: not found", updateId);
            return RestResponse.notFound();
        }

        if (update.getThread().getId().longValue() != id) {
            log.info("Attachment lookup {} failed: incorrect thread ({}, expected {})", updateId, id, update.getThread().getId());
            return RestResponse.notFound();
        }

        // TODO Check if user has thread access

        if (!(update instanceof ThreadUpdate.ThreadAttachment attachment)) {
            log.info("Attachment lookup {} failed: not an attachment", updateId);
            return RestResponse.status(Status.BAD_REQUEST);
        }

        if (!attachment.isFileReady()) {
            log.info("Attachment lookup {} failed: file not ready (status is {})", updateId, attachment.getFileStatus());
            return RestResponse.status(Status.BAD_REQUEST);
        }

        log.info("User [{}] requested [{}]: {}", securityIdentity.getPrincipal().getName(), updateId, attachment.getFilePath());

        return ResponseBuilder.ok(attachment.getFilePath(), MEDIATYPE_WEBP)
            .header(HttpHeaders.CONTENT_DISPOSITION, attachment.getFilename())
            .cacheControl(CACHE_CONTROL_LONG_BUT_PRIVATE)
            .build();
    }

    @POST
    @Transactional
    @Path("/{id}/message")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public RestResponse<Void> addThreadMessage(
        @Positive @PathParam("id") Long id,
        @Valid @BeanParam ThreadCommentRequest body
    ) {
        var thread = threadRepository.findById(id);
        if (thread == null)
            return RestResponse.notFound();

        var files = body.files();
        if (!files.isEmpty()) {
            log.info("{} files were present", files.size());
            for (var file : body.files()) {
                log.info("Name = {}", file.name());
                log.info("Upload name = {}", file.uploadedFile().toString());
                log.info("Filename = {}", file.fileName());
            }
        } else {
            log.info("No files were present");
        }

        var update = (ThreadUpdate.ThreadMessage) threadService.createUpdate(thread, UpdateType.Message);
        update.setMessage(body.message());

        for (var file : body.files()) {
            var fileUpdate = threadService.createAttachmentUpdate(thread, file);
            fileUpdate.setGroupKey(update.getGroupKey());
            log.info("Created {}", fileUpdate);
        }

        return RestResponse.status(Status.RESET_CONTENT);
    }
}
