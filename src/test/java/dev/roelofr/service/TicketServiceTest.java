package dev.roelofr.service;

import dev.roelofr.DomainHelper;
import dev.roelofr.domain.enums.AttachmentType;
import dev.roelofr.domain.enums.TicketType;
import dev.roelofr.repository.TicketRepository;
import dev.roelofr.rest.request.TicketCreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock
    TicketAttachmentService attachmentService;

    @Mock
    TicketRepository ticketRepository;

    @Mock
    AuthenticationService authenticationService;

    @Mock
    VendorService vendorService;

    @InjectMocks
    TicketService ticketService;

    DomainHelper domainHelper = DomainHelper.getInstance();

    @Test
    void createFromRequest() {
        var user = domainHelper.randomUser();
        var vendor = domainHelper.randomVendor();

        var input = new TicketCreateRequest(
            vendor.getId(),
            "Hello World",
            TicketType.Generic,
            Map.ofEntries(
                Map.entry("field", "Value"),
                Map.entry("boolean", false)
            )
        );

        // GIVEN
        given(authenticationService.getCurrentUser()).willReturn(Optional.of(user));
        given(vendorService.getVendor(vendor.getId())).willReturn(vendor);

        // WHEN
        var result = ticketService.createFromRequest(input);

        // VERIFY
        assertNotNull(result);
        assertInstanceOf(Ticket.class, result);
        assertSame(user, result.getCreator());
        assertSame(vendor, result.getVendor());
        assertSame("Hello World", result.getDescription());

        verify(ticketRepository, times(1)).persist(result);
        verify(attachmentService, times(1)).create(same(result), eq(AttachmentType.Created));
    }

    @Test
    void addComment() {
    }

    @Test
    void assignTo() {
    }

    @Test
    void resolve() {
    }
}
