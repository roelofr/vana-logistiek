package dev.roelofr.service;

import dev.roelofr.domain.District;
import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.AttachmentType;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.repository.TicketRepository;
import dev.roelofr.rest.request.TicketCreateRequest;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.UnauthorizedException;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final DistrictService districtService;
    private final TicketAttachmentService attachmentService;
    private final AuthenticationService authenticationService;
    private final VendorService vendorService;

    public List<Ticket> list() {
        return ticketRepository.listAll(
            Sort.by("id", Sort.Direction.Descending)
        );
    }

    public List<Ticket> listByDistrict(@Nonnull District district) {
        return ticketRepository.list("district = ?", district, Sort.by("id", Sort.Direction.Descending));
    }

    public List<Ticket> listByDistrictName(String districtName) {
        var district = districtService.findByName(districtName);
        if (district == null)
            throw new IllegalArgumentException("District was not found!");

        return listByDistrict(district);
    }

    public List<Ticket> listByVendor(@Nonnull Vendor vendor) {
        return ticketRepository
            .find("#Ticket.ByVendorWithOwner", vendor)
            .list();
    }

    public List<Ticket> listByUser(User user) {
        return ticketRepository
            .find("#Ticket.ByUserWithOwner", user)
            .list();
    }

    public Optional<Ticket> findById(long id) {
        return ticketRepository.findByIdOptional(id);
    }

    @Transactional
    public Ticket createFromRequest(@Valid TicketCreateRequest request) {
        var user = authenticationService.getCurrentUser().orElse(null);
        if (user == null)
            throw new UnauthorizedException();

        var vendor = vendorService.getVendor(request.vendorId());
        if (vendor == null)
            throw new NotFoundException("Vendor was not found");

        var ticket = Ticket.builder()
            .creator(user)
            .description(request.description())
            .vendor(vendor)
            .build();

        ticketRepository.persist(ticket);

        attachmentService.create(ticket, AttachmentType.Created);

        return ticket;
    }

    public void addComment(Ticket ticket, String comment) {
        var attachment = attachmentService.create(ticket, AttachmentType.StatusChange);
        if (comment != null && !comment.isBlank())
            attachment.setDescription(comment);

        if (attachment.getUser().is(ticket.getCreator()))
            return;

        ticket.setStatus(TicketStatus.Updated);
    }

    public void assignTo(Ticket ticket, User assignee, String comment) {
        ticket.setAssignee(assignee);
        ticket.setStatus(TicketStatus.Assigned);

        var attachment = attachmentService.create(ticket, AttachmentType.Assignment);
        if (comment != null && !comment.isBlank())
            attachment.setDescription(comment);

    }

    public void resolve(Ticket ticket, String comment) {
        ticket.setStatus(TicketStatus.Resolved);
        ticket.setCompletedAt(LocalDateTime.now());

        var attachment = attachmentService.create(ticket, AttachmentType.StatusChange);
        if (comment != null && !comment.isBlank())
            attachment.setDescription(comment);
    }
}
