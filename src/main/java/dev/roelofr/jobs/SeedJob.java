package dev.roelofr.jobs;

import com.github.javafaker.Faker;
import dev.roelofr.config.AppConfig;
import dev.roelofr.domain.District;
import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.TicketRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.repository.VendorRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SeedJob {
    private static final int USER_COUNT = 10;
    private static final int VENDOR_COUNT = 100;

    private final AppConfig appConfig;

    private final DistrictRepository districtRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;

    private final Faker faker = new Faker();

    private List<District> districts;

    @Transactional
    public void seedOnStartup(@Observes StartupEvent startupEvent) {
        if (!appConfig.shouldSeed())
            return;

        districts = districtRepository.listAll();

        userRepository.deleteAll();
        vendorRepository.deleteAll();
        ticketRepository.deleteAll();

        var users = createUsers();

        var vendors = createVendors();

        createTicketsWithUsersAndVendors(users, vendors);
    }

    private TicketStatus randomTicketStatus() {
        var vals = TicketStatus.values();
        return vals[faker.random().nextInt(vals.length)];
    }

    private District randomDistrict() {
        return districts.get(faker.random().nextInt(districts.size()));
    }

    List<User> createUsers() {
        final int userCount = faker.random().nextInt(1, USER_COUNT);

        final var users = new ArrayList<User>(userCount);

        for (int i = 0; i < userCount; i++) {
            var user = User.builder()
                .email(faker.internet().safeEmailAddress())
                .name(faker.name().fullName())
                .password("password")
                .district(randomDistrict())
                .build();

            log.info("Created user {} ({})", user.getName(), user.getEmail());

            users.add(user);
        }

        userRepository.persist(users);

        return users;
    }

    List<Vendor> createVendors() {
        final int vendorCount = faker.random().nextInt(1, VENDOR_COUNT);

        final var vendors = new ArrayList<Vendor>(vendorCount);

        for (int i = 0; i < vendorCount; i++) {
            int vendorNumber = faker.random().nextInt(100, 1399);
            String vendorSuffix = faker.letterify("?", false);

            String number = Integer.toString(vendorNumber, 10) + (faker.random().nextBoolean() ? vendorSuffix : "");

            var vendor = Vendor.builder()
                .name(faker.company().name())
                .number(number)
                .district(randomDistrict())
                .build();

            log.info("Created vendor {}: {}", vendor.getNumber(), vendor.getName());

            vendors.add(vendor);
        }

        vendorRepository.persist(vendors);

        return vendors;
    }

    void createTicketsWithUsersAndVendors(List<User> users, List<Vendor> vendors) {
        final var userCount = users.size();
        final var vendorCount = vendors.size();
        final var maxTicketsPerUser = (int) Math.min(Math.floor((double) vendorCount / userCount * 1.25), 1);

        log.info("Will create {} tickets per user, peaking at {} tickets", maxTicketsPerUser, maxTicketsPerUser * userCount);

        var tickets = new ArrayList<Ticket>(maxTicketsPerUser * userCount);

        for (var user : users) {
            final var ticketCount = faker.random().nextInt(0, maxTicketsPerUser);

            log.info("Creating {} tickets for {}", ticketCount, user.getName());

            for (var i = 0; i < ticketCount; i++) {
                var ticket = Ticket.builder()
                    .creator(user)
                    .vendor(vendors.get(faker.random().nextInt(vendors.size())))
                    .description(faker.lorem().sentence())
                    .status(randomTicketStatus())
                    .build();

                log.info("Created ticket for vendor [{}] with status [{}]", ticket.getVendor().getName(), ticket.getStatus());

                tickets.add(ticket);
            }
        }

        ticketRepository.persist(tickets);
    }
}
