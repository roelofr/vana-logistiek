package dev.roelofr.repository;

import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.TicketAttachment;
import dev.roelofr.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TicketAttachmentRepository implements PanacheRepository<TicketAttachment> {
    public List<TicketAttachment> findForTicket(Ticket ticket) {
        return find("ticket", Sort.by("createdAt", Direction.Descending), ticket).list();
    }

    public List<TicketAttachment> findForTicket(User user) {
        return find("#TicketAttachment.findForUser", user).list();
    }
}
