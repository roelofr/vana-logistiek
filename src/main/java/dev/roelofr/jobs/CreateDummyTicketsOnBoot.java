package dev.roelofr.jobs;

import com.github.javafaker.Faker;
import dev.roelofr.Constants;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.repository.TicketRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.repository.VendorRepository;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@ApplicationScoped
public class CreateDummyTicketsOnBoot {
    private static final long WANTED_TICKETS = 100L;

    @Inject
    TicketRepository ticketRepository;

    @Inject
    VendorRepository vendorRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    LaunchMode launchMode;

    Faker faker;

    void createTicketsOnBoot(@Observes StartupEvent startupEvent) {
        if (launchMode.equals(LaunchMode.DEVELOPMENT))
            this.createTickets();
    }

    @Transactional
    void createTickets() {
        var ticketCount = new AtomicLong(ticketRepository.count());
        if (ticketCount.get() >= WANTED_TICKETS) {
            log.info("Not provisioning tickets. Targets met.");
            return;
        }

        var firstUser = userRepository.findAll().firstResult();
        if (firstUser == null) {
            log.error("Failed to provision tickets! No users known.");
        }

        var allVendors = vendorRepository.listAll();
        if (allVendors.isEmpty()) {
            log.error("Failed to provision tickets! No vendors known.");
            return;
        }

        faker = Faker.instance(Constants.LocaleDutch);

        while (ticketCount.getAndIncrement() < WANTED_TICKETS) {
            var vendor = allVendors.get(faker.random().nextInt(allVendors.size()));
            createTicket(vendor, firstUser);
        }
    }

    void createTicket(Vendor vendor, User owner) {
        var status = randomStatus();
        var description = randomDescription();

        var ticket = Ticket.builder()
            .creator(owner)
            .vendor(vendor)
            .status(status)
            .description(description)
            .build();

        ticket.setCreatedAt(pastDate());

        if (status != TicketStatus.Created)
            ticket.setUpdatedAt(dateAfter(ticket.getCreatedAt()));

        if (status == TicketStatus.Resolved)
            ticket.setCompletedAt(dateAfter(ticket.getUpdatedAt()));

        ticketRepository.persist(ticket);

        log.info("Persisted ticket {} for {} named {}", ticket.getId(), ticket.getVendor().getNumber(), ticket.getDescription());
    }

    String randomDescription() {
        var words = faker.lorem().words(faker.random().nextInt(1, 7));
        var sentence = String.join(" ", words);
        return sentence.length() < 100 ? sentence : sentence.substring(0, 97) + "...";
    }

    TicketStatus randomStatus() {
        var opts = TicketStatus.values();
        return opts[faker.random().nextInt(opts.length)];
    }

    LocalDateTime pastDate() {
        var date = faker.date().past(7, TimeUnit.DAYS);
        return LocalDateTime.ofInstant(date.toInstant(), Constants.ZoneIdAmsterdam);
    }

    LocalDateTime dateAfter(LocalDateTime reference) {
        return reference.plusMinutes(faker.random().nextInt(2, 72 * 60 * 60));
    }
}
