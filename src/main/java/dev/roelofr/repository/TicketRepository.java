package dev.roelofr.repository;

import dev.roelofr.domain.District;
import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {
    public Optional<Ticket> findByVendorId(int vendorId) {
        return find("vendor.id = ?1", vendorId)
            .firstResultOptional();
    }

    public List<Ticket> listForDetail() {
        return find("""
            """).firstResult();
    }

    public List<Ticket> findForDistrict(District district) {
        return find("district", district).list();
    }

    public List<Ticket> findForUser(User user) {
        return find("creator = ? OR assignee = ?", user).list();
    }
}
