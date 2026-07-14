package dev.roelofr.domains.chat;

import dev.roelofr.domains.chat.model.ChatEntryRepository;
import dev.roelofr.domains.chat.model.ChatFile;
import dev.roelofr.domains.chat.model.FileStatus;
import dev.roelofr.domains.users.model.User;
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
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@Authenticated
@Path("/files")
@RequiredArgsConstructor
@Tags({@Tag(name = "Chat")})
public class AttachmentResource {
    private static final MediaType MEDIATYPE_WEBP = MediaType.valueOf("image/webp");
    private static final CacheControl CACHE_CONTROL_LONG_BUT_PRIVATE;

    static {
        CACHE_CONTROL_LONG_BUT_PRIVATE = new CacheControl();
        CACHE_CONTROL_LONG_BUT_PRIVATE.setPrivate(true);
        CACHE_CONTROL_LONG_BUT_PRIVATE.setMaxAge((int) Duration.ofDays(300).toSeconds());
    }

    private final ChatEntryRepository chatEntryRepository;
    private final ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @Transactional
    @Path("/chat/{id}/image/{updateid}/{filename}")
    @Operation(
        operationId = "chatAttachmentShow",
        description = "Shows the attachment with the given id from the given chat"
    )
    @APIResponseSchema(FileInputStream.class)
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Attachment contents", content = {@Content(mediaType = "image/webp")}),
        @APIResponse(responseCode = "404", description = "Attachment not found, or not part of the given chat"),
        @APIResponse(responseCode = "403", description = "Chat not accessible to user"),
        @APIResponse(responseCode = "410", description = "Attachment was deleted or corrupted"),
        @APIResponse(responseCode = "415", description = "Attachment is not yet processed"),
    })
    public RestResponse<java.nio.file.Path> showThreadAttachmentImage(
        @Context User user,
        @Positive @PathParam("id") Long id,
        @Positive @PathParam("updateid") Long updateId,
        @NotBlank @PathParam("filename") String filename
    ) {
        // ID Must exist
        var chatEntry = chatEntryRepository.findById(updateId);
        if (chatEntry == null) {
            log.info("Attachment lookup {} failed: not found", updateId);
            return RestResponse.notFound();
        }

        // Thread must match
        if (!Objects.equals(chatEntry.getChat().getId(), id)) {
            log.info("Attachment lookup {} failed: incorrect thread (expects {}, given {})", updateId, id, chatEntry.getChat().getId());
            return RestResponse.notFound();
        }

        // Check if user has thread access
        if (!chatService.isVisibleForUser(chatEntry.getChat(), user))
            return RestResponse.status(Response.Status.FORBIDDEN);

        // Check type
        if (!(chatEntry instanceof ChatFile chatFile)) {
            log.info("Attachment lookup {} failed: update is a {}, expected attachment", id, chatEntry);
            return RestResponse.notFound();
        }

        // Check readiness
        if (chatFile.isFileCorrupted()) {
            log.info("Attachment lookup {} failed: file not ready (status is {})", updateId, chatFile.getFileStatus());
            return RestResponse.status(RestResponse.Status.GONE);
        }

        if (!chatFile.isFileReady()) {
            log.info("Attachment lookup {} failed: file not ready (status is {})", updateId, chatFile.getFileStatus());
            return RestResponse.status(RestResponse.Status.UNSUPPORTED_MEDIA_TYPE);
        }

        // Return file
        var file = new File(chatFile.getPath());

        log.info("User [{}] requested [{}]: {}", securityIdentity.getPrincipal().getName(), updateId, file.getAbsolutePath());

        if (!file.exists()) {
            chatFile.setFileStatus(FileStatus.Corrupted);

            log.error("Attachment lookup {} failed: file does not exist", updateId);
            return RestResponse.status(RestResponse.Status.GONE);
        }

        return RestResponse.ResponseBuilder.ok(file.toPath())
            .header(HttpHeaders.CONTENT_DISPOSITION, chatFile.getFilename())
            .build();
    }
}
