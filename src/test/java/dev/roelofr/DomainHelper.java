package dev.roelofr;

import dev.roelofr.domain.District;
import dev.roelofr.domain.TestDistrict;
import dev.roelofr.domain.TestTicket;
import dev.roelofr.domain.TestTicketAttachment;
import dev.roelofr.domain.TestUser;
import dev.roelofr.domain.TestVendor;
import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.TicketAttachment;
import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.domain.enums.AttachmentType;

import java.util.List;
import java.util.Random;

public class DomainHelper {
    public final static String EMAIL_ADMIN = "admin@example.com";
    public final static String EMAIL_USER = "user@example.com";
    public final static String EMAIL_CP = "cp@example.com";

    public final static Long DISTRICT_ADMIN = null;
    public final static Long DISTRICT_USER = 5001L;
    public final static Long DISTRICT_CP = 5002L;

    final static List<District> mockDistricts;
    final static List<User> mockUsers;
    final static List<Vendor> mockVendors;

    final static Random rng = new Random();

    static {
        mockDistricts = List.of(
            TestDistrict.make("Test One"),
            TestDistrict.make("Test Two"),
            TestDistrict.make("Test Three")
        );

        mockUsers = List.of(
            TestUser.make("Test User One", mockDistricts.get(0)),
            TestUser.make("Test User Two", mockDistricts.get(1)),
            TestUser.make("Test CP One"),
            TestUser.make("Test CP Two"),
            TestUser.make("Test Admin", mockDistricts.get(0))
        );

        mockVendors = List.of(
            TestVendor.make("Happy Dreams", "100a", mockDistricts.get(0).getName()),
            TestVendor.make("Happier Dreams", "203g", mockDistricts.get(1).getName()),
            TestVendor.make("Angry Swords", "1052", mockDistricts.get(2).getName()),
            TestVendor.make("Angry Sables", "1053", mockDistricts.get(2).getName()),
            TestVendor.make("Angry Shisha", "1308", mockDistricts.get(2).getName())
        );
    }

    public static DomainHelper getInstance() {
        return new DomainHelper();
    }

    public User randomUser() {
        return mockUsers.get(rng.nextInt(mockUsers.size()));
    }

    public District randomDistrict() {
        return mockDistricts.get(rng.nextInt(mockDistricts.size()));
    }

    public Vendor randomVendor() {
        return mockVendors.get(rng.nextInt(mockVendors.size()));
    }

    public User dummyUser(String email) {
        var user = TestUser.make("Test User");
        user.setEmail(email);
        return user;
    }

    public Ticket dummyTicket(String description, Vendor vendor, User creator) {
        var ticket = TestTicket.make(description, vendor);
        ticket.setCreator(creator);
        return ticket;
    }

    public Ticket dummyTicket(String summary) {
        return TestTicket.make(summary, randomVendor());
    }

    public TicketAttachment dummyTicketAttachment(Ticket ticket, AttachmentType attachmentType, String description) {
        return TestTicketAttachment.make(ticket, attachmentType, randomUser(), description);
    }

    public TicketAttachment dummyTicketAttachment(Ticket ticket) {
        return TestTicketAttachment.make(ticket, randomUser());
    }
}
