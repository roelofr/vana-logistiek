package dev.roelofr.jobs;

import dev.roelofr.domain.TicketMetric;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.repository.TicketMetricRepository;
import dev.roelofr.repository.TicketRepository;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.format.DateTimeFormatter.ISO_TIME;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CreateTicketMetric {
    final TicketRepository ticketRepository;
    final TicketMetricRepository ticketMetricRepository;
    final LaunchMode launchMode;

    @Scheduled(every = "5m")
    void createTicketMetrics() {
        if (launchMode.equals(LaunchMode.TEST))
            return;

        doCreateTicketMetrics();
    }

    @Transactional
    void doCreateTicketMetrics() {
        var truncatedTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var lowerBound = truncatedTime.minusMinutes(truncatedTime.getMinute() % 15);
        var upperBound = LocalDateTime.now();

        log.info("Collecting metrics since {}", lowerBound.format(ISO_TIME));

        var totalTickets = ticketRepository.count();
        var openTickets = ticketRepository.find("status != ?1", TicketStatus.Resolved).count();

        var createdTickets = ticketRepository.find("createdAt >= ?1", lowerBound).count();
        var updatedTickets = ticketRepository.find("createdAt < ?1 and updatedAt >= ?1", lowerBound).count();
        var resolvedTickets = ticketRepository.find("completedAt >= ?1", lowerBound).count();

        var metric = TicketMetric.builder()
            .plotAt(truncatedTime)
            .totalCount(totalTickets)
            .openCount(openTickets)
            .createdCount(createdTickets)
            .updatedCount(updatedTickets)
            .resolvedCount(resolvedTickets)
            .build();

        log.debug("Metrics recorded at {}: {}", upperBound.format(ISO_TIME), metric);

        ticketMetricRepository.persist(metric);
    }
}
