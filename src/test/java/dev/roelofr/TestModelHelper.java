package dev.roelofr;

import dev.roelofr.domain.Ticket;
import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.TicketRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.repository.VendorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
public class TestModelHelper {
    public static final long VENDOR_ONE = 5001;
    public static final long VENDOR_TWO = 5002;
    public static final long VENDOR_THREE = 5003;

    public static final long DISTRICT_ONE_ID = 5001;
    public static final long DISTRICT_TWO_ID = 5002;
    public static final long DISTRICT_THREE_ID = 5003;

    public static final String DISTRICT_ONE = "test-rood";
    public static final String DISTRICT_TWO = "test-blauw";
    public static final String DISTRICT_THREE = "test-groen";

    public static final long USER_ONE_ID = 5001;

    private final DistrictRepository districtRepository;

    private final VendorRepository vendorRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public void deleteVendors() {
        vendorRepository.deleteAll();
    }

    public Vendor createVendor(String number, String name, String district) {
        var districtObject = districtRepository.findByName(district).orElseThrow(() -> new IllegalArgumentException("District " + district + " not found"));

        var vendor = Vendor.builder()
            .number(number)
            .name(name)
            .district(districtObject)
            .build();

        vendorRepository.persist(vendor);

        return vendor;
    }

    public Ticket ticketHelper(String summary, long vendorId, long userId) {
        var ticket = Ticket.builder()
            .description(summary)
            .vendor(vendorRepository.findById(vendorId))
            .creator(userRepository.findById(userId))
            .build();

        ticketRepository.persist(ticket);

        return ticket;
    }

    public Ticket ticketHelper(String summary) {
        return ticketHelper(summary, USER_ONE_ID, VENDOR_ONE);
    }
}
