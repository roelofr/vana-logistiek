package dev.roelofr.domains.issue;

import dev.roelofr.Constants;
import dev.roelofr.domains.chat.ChatService;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatType;
import dev.roelofr.domains.issue.dto.CreateIssueRequest;
import dev.roelofr.domains.users.GroupService;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

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

        var vendor = vendorService.getVendor(request.vendorId()).orElse(null);
        if (vendor == null) {
            log.error("Vendor with ID {} not found", request.vendorId());
            return RestResponse.status(RestResponse.Status.NOT_FOUND);
        }

        var cpGroup = groupService.findByLabel(Constants.Groups.CP).orElse(null);
        if (cpGroup == null) {
            log.error("CP group {} not found", Constants.Groups.CP);
            return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR);
        }

        var relevantGroup = user.getGroups().stream()
            .filter(group -> group.getDistricts() != null && !group.getDistricts().isEmpty())
            .findFirst()
            .orElse(null);

        Chat createdChat = (relevantGroup != null)
            ? chatService.createChat(ChatType.Issue, "Issue", null, List.of(relevantGroup, cpGroup))
            : chatService.createChat(ChatType.Issue, "Issue", List.of(user), List.of(cpGroup));

        var attachedIssue = issueService.create(createdChat, vendor);

        createdChat.setTitle(String.format("#%03d: %s", attachedIssue.getId(), request.title()));

        return RestResponse.ok(attachedIssue);
    }
}
