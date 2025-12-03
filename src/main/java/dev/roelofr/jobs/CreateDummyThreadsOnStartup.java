package dev.roelofr.jobs;

import com.github.javafaker.Faker;
import dev.roelofr.Constants;
import dev.roelofr.domain.Thread;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.repository.ThreadRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.repository.VendorRepository;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@ApplicationScoped
public class CreateDummyThreadsOnStartup {
    private static final long WANTED_THREADS = 100L;

    @Inject
    ThreadRepository threadRepository;

    @Inject
    VendorRepository vendorRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    LaunchMode launchMode;

    Faker faker;

    Random random = new Random();

    @Startup
    void createTicketsOnBoot() {
        if (launchMode.equals(LaunchMode.DEVELOPMENT))
            this.createThreads();
    }

    @Transactional
    void createThreads() {
        var ticketCount = new AtomicLong(threadRepository.count());
        if (ticketCount.get() >= WANTED_THREADS) {
            log.info("Not provisioning tickets. Targets met.");
            return;
        }

        var userList = userRepository.findAll().list();
        if (userList.isEmpty()) {
            log.error("Failed to provision tickets! No users known.");
            return;
        }

        var allVendors = vendorRepository.listAll();
        if (allVendors.isEmpty()) {
            log.error("Failed to provision tickets! No vendors known.");
            return;
        }

        faker = Faker.instance(Constants.LocaleDutch);

        while (ticketCount.getAndIncrement() < WANTED_THREADS) {
            var vendor = allVendors.get(faker.random().nextInt(allVendors.size()));
            createThread(vendor, userList);
        }
    }

    void createThread(Vendor vendor, List<User> users) {
        var owner = users.get(random.nextInt(users.size()));
        var status = randomStatus();
        var description = randomDescription();

        var thread = Thread.builder()
            .user(owner)
            .team(owner.getTeam())
            .subject(description)
            .build();

        threadRepository.persist(thread);

        thread.setCreatedAt(pastDate());

        if (status != TicketStatus.Created)
            thread.setUpdatedAt(dateAfter(thread.getCreatedAt()));

        if (status == TicketStatus.Resolved)
            thread.setResolvedAt(dateAfter(thread.getUpdatedAt()));

        log.info("Persisted thread {} for {} named {}", thread.getId(), thread.getVendor().getNumber(), thread.getSubject());
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
