package dev.roelofr.domains.vendor;

import dev.roelofr.config.Roles;
import dev.roelofr.domains.chat.ChatService;
import dev.roelofr.domains.issue.Issue;
import dev.roelofr.domains.issue.IssueService;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Authenticated
@Path("/vendors")
@RequiredArgsConstructor
@Tags({@Tag(name = "Vendors")})
public class VendorResource {
    private final VendorService vendorService;
    private final IssueService issueService;
    private final ChatService chatService;

    @GET
    @Transactional
    @Operation(operationId = "vendorList", summary = "Lists all vendors")
    public RestResponse<List<Vendor>> getVendors() {
        return RestResponse.ok(
            vendorService.listVendors()
        );
    }

    @GET
    @Path("/{id}")
    @Transactional
    @Operation(operationId = "vendorFindById", summary = "Finds a single vendor by it's ID")
    public RestResponse<Vendor> getVendor(@Positive @PathParam("id") long vendorId) {
        var vendor = vendorService.getVendor(vendorId).orElse(null);
        if (vendor == null)
            return RestResponse.notFound();

        return RestResponse.ok(vendor);
    }

    @GET
    @Transactional
    @Path("/{id}/issues")
    @Operation(operationId = "vendorIssuesById", summary = "Finds issues associated with this vendor, admins can request to view all issues.")
    public RestResponse<List<Issue>> getVendorIssues(
        @Context User user,
        @Positive @PathParam("id") long vendorId,
        @QueryParam("all") @DefaultValue("false") Boolean showAll
    ) {
        var vendor = vendorService.getVendor(vendorId).orElse(null);
        if (vendor == null)
            return RestResponse.notFound();

        var issues = issueService.findByVendor(vendor);
        if (issues.isEmpty())
            return RestResponse.ok(List.of());

        if (showAll && user.hasRole(Roles.Admin))
            return RestResponse.ok(issues);
        if (showAll)
            return RestResponse.status(RestResponse.Status.FORBIDDEN);

        var availableChatIds = chatService.findIdsByUser(user);
        return RestResponse.ok(
            issues.stream()
                .filter(issue -> availableChatIds.contains(issue.getId()))
                .toList()
        );
    }
}
