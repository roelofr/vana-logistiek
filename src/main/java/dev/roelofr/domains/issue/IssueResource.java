package dev.roelofr.domains.issue;

import dev.roelofr.Constants;
import dev.roelofr.Roles;
import dev.roelofr.domains.chat.ChatEntryService;
import dev.roelofr.domains.chat.ChatService;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatState;
import dev.roelofr.domains.chat.model.ChatType;
import dev.roelofr.domains.chat.model.SystemMessageType;
import dev.roelofr.domains.issue.dto.CreateIssueRequest;
import dev.roelofr.domains.users.GroupService;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Authenticated
@Path("/issues")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Tags({@Tag(name = "Issues")})
public class IssueResource {
    private final ChatService chatService;
    private final VendorService vendorService;
    private final GroupService groupService;
    private final IssueService issueService;
    private final ChatEntryService chatEntryService;

    @GET
    @Operation(
        operationId = "issueList",
        description = "Create a new issue, and it's corresponding chat"
    )
    public RestResponse<List<Issue>> list() {
        return RestResponse.status(RestResponse.Status.NOT_IMPLEMENTED);
    }

    @POST
    @Transactional
    @Operation(
        operationId = "issueCreate",
        description = "Create a new issue, and it's corresponding chat"
    )
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public RestResponse<Issue> create(@Context User user, @Valid CreateIssueRequest request) {
        if (user.getId() == null) {
            log.error("Got user without ID, somehow");
            return RestResponse.status(RestResponse.Status.UNAUTHORIZED);
        }

        Vendor vendor = request.vendorId() == null ? null : vendorService.getVendor(request.vendorId()).orElseThrow(() -> new BadRequestException("Standhouder bestaat niet"));

        var chatUsers = new ArrayList<User>();
        var chatGroups = new ArrayList<Group>();

        // FIND CP GROUP
        var cpGroup = groupService.findByLabel(Constants.Groups.CP).orElse(null);
        if (cpGroup == null) {
            log.error("CP group {} not found", Constants.Groups.CP);
            return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR);
        }
        chatGroups.add(cpGroup);

        // ADD USER VIA GROUP OR DIRECTLY
        var relevantGroup = user.getGroups().stream()
            .filter(group -> group.getDistricts() != null && !group.getDistricts().isEmpty())
            .findFirst();

        if (relevantGroup.isPresent())
            chatGroups.add(relevantGroup.get());
        else
            chatUsers.add(user);

        // ADD GROUP OWNING THIS VENDOR
        if (vendor != null && vendor.getDistrict() != null && vendor.getDistrict().getGroup() != null)
            chatGroups.add(vendor.getDistrict().getGroup());

        // Create chat
        Chat createdChat = chatService.createChat(ChatType.Issue, "Issue", chatUsers, chatGroups);

        // Create system message
        chatEntryService.createSystemMessage(createdChat, SystemMessageType.Created, "Melding aangemaakt", user);

        // Attach issue
        var attachedIssue = issueService.create(createdChat, vendor, request.location());

        createdChat.setTitle(String.format("#%03d: %s", attachedIssue.getId(), request.title()));

        return RestResponse.ok(attachedIssue);
    }

    @POST
    @Transactional
    @Path("/{id}/resolve")
    @Operation(
        operationId = "issueResolve",
        description = "Mark a chat as resolved, does not end the chat."
    )
    @RolesAllowed({Roles.Admin, Roles.CentralePost})
    public RestResponse<Void> resolve(@Context User user, @PathParam("id") @Positive long issueId) {
        var issue = issueService.findById(issueId);
        if (issue == null)
            return RestResponse.notFound();

        var chat = issue.getChat();
        if (chat == null)
            return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR);

        if (ChatState.Closed.equals(chat.getState()))
            return RestResponse.status(RestResponse.Status.RESET_CONTENT);

        // Create system message
        chatEntryService.createSystemMessage(chat, SystemMessageType.Resolved, "Melding gemarkeerd als opgelost", user);
        issue.setResolvedAt(LocalDateTime.now());

        return RestResponse.ok();
    }
}
