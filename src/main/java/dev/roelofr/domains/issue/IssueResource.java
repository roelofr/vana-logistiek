package dev.roelofr.domains.issue;

import dev.roelofr.domains.issue.dto.CreateIssueDto;
import dev.roelofr.domains.issue.model.Issue;
import dev.roelofr.domains.issue.model.IssueService;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Authenticated
@Path("/issue")
@RequiredArgsConstructor
public class IssueResource {
    private final IssueService issueService;
    private final VendorService vendorService;

    @RequestScoped
    User user;

    @GET
    public RestResponse<List<Issue>> getIssues(@QueryParam("all") Boolean all) {
        return RestResponse.ok(
            all == true ? issueService.getAllSorted() : issueService.getSortedForUser(user)
        );
    }

    @POST
    @Transactional
    public RestResponse<Issue> createIssue(CreateIssueDto dto) {
        var vendor = vendorService.getVendor(dto.vendorId())
            .orElseThrow(() -> new NotFoundException(
                String.format("Failed to find vendor %d", dto.vendorId())
            ));

        var issue = issueService.createIssue(vendor, user, dto.subject());

        // TODO Create chat

        // TODO Link chat to issue

        var location = UriBuilder.fromMethod(IssueResource.class, "getIssue")
            .build(issue.getId());

        return RestResponse.created(location);
    }

    @GET
    @Path("/{id}")
    @Transactional
    public RestResponse<Issue> getIssue(@PathParam("id") @Positive long issueId) {
        Issue issue = issueService.getIssue(issueId)
            .orElseThrow(() -> new NotFoundException(
                String.format("Failed to find issue %d", issueId)
            ));

        if (!issueService.isParticipant(issue, user))
            throw new ForbiddenException("You are not a participant in this issue");

        return RestResponse.ok(issue);
    }

}
