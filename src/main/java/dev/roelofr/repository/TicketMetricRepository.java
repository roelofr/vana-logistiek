package dev.roelofr.repository;

import dev.roelofr.domain.TicketMetric;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

@ApplicationScoped
public class TicketMetricRepository implements PanacheRepository<TicketMetric> {
    public List<TicketMetric> findSince(TemporalAmount offset) {
        return this.find("createdAt > ?1 ordered by createdAt asc", LocalDateTime.now().minus(offset))
            .list();
    }
}
