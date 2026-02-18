package dev.roelofr.rest.resources;

import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.enums.FileStatus;
import dev.roelofr.repository.ThreadUpdateRepository;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;

@Slf4j
@Authenticated
@Path("/files")
@RequiredArgsConstructor
public class AttachmentResource {
    private static final MediaType MEDIATYPE_WEBP = MediaType.valueOf("image/webp");
    private static final CacheControl CACHE_CONTROL_LONG_BUT_PRIVATE;

    static {
        CACHE_CONTROL_LONG_BUT_PRIVATE = new CacheControl();
        CACHE_CONTROL_LONG_BUT_PRIVATE.setPrivate(true);
        CACHE_CONTROL_LONG_BUT_PRIVATE.setMaxAge((int) Duration.ofDays(300).toSeconds());
    }

    private final ThreadUpdateRepository threadUpdateRepository;

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @Transactional
    @Path("/thread/{id}/image/{updateid}/{filename}")
    public RestResponse<FileInputStream> showThreadAttachmentImage(
        @Positive @PathParam("id") Long id,
        @Positive @PathParam("updateid") Long updateId,
        @NotBlank @PathParam("filename") String filename
    ) {
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
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }

        if (!attachment.isFileReady()) {
            log.info("Attachment lookup {} failed: file not ready (status is {})", updateId, attachment.getFileStatus());
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }

        var file = attachment.getFilePath().toFile();

        log.info("User [{}] requested [{}]: {}", securityIdentity.getPrincipal().getName(), updateId, file.getAbsolutePath());

        if (!file.exists()) {
            attachment.setFileStatus(FileStatus.Corrupted);

            log.error("Attachment lookup {} failed: file does not exist", updateId);
            return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR);
        }

        try (var stream = new FileInputStream(file)) {
            return RestResponse.ResponseBuilder.ok(stream, MEDIATYPE_WEBP)
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment.getFilename())
                .build();
        } catch (IOException e) {
            log.error("Attachment lookup {} errored: {}", updateId, e.getMessage());
            return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
