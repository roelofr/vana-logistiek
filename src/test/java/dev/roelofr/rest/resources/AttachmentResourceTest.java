package dev.roelofr.rest.resources;

import dev.roelofr.DomainHelper;
import dev.roelofr.config.Roles;
import dev.roelofr.domain.enums.AttachmentType;
import dev.roelofr.repository.TicketAttachmentRepository;
import dev.roelofr.repository.TicketRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.BDDMockito.given;

@QuarkusTest
class AttachmentResourceTest {
    @InjectMock
    TicketRepository ticketRepository;
    @InjectMock
    TicketAttachmentRepository ticketAttachmentRepository;

    DomainHelper domainHelper = new DomainHelper();

    Ticket ticket;

    List<TicketAttachment> ticketAttachments = new ArrayList<>();

    @BeforeEach
    void setupTicket() {
        ticket = domainHelper.dummyTicket("I am a dummy");

        given(ticketRepository.findByIdOptional(ticket.getId()))
            .willReturn(Optional.of(ticket));

        given(ticketAttachmentRepository.findForTicket(ticket))
            .will(invocation -> ticketAttachments);
    }

    @Test
    void testUnauthenticated() {
        assert ticketAttachments.isEmpty();

        when().get("/ticket/{ticketId}/attachment/", ticket.getId())
            .then()
            .assertThat()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = DomainHelper.EMAIL_NEW)
    void testAuthenticatedNewAccount() {
        assert ticketAttachments.isEmpty();

        when().get("/ticket/{ticketId}/attachment/", ticket.getId())
            .then()
            .assertThat()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = DomainHelper.EMAIL_USER, roles = {Roles.User})
    void getAttachmentsEmpty() {
        assert ticketAttachments.isEmpty();

        when().get("/ticket/{ticketId}/attachment/", ticket.getId())
            .then()
            .assertThat()
            .statusCode(200)
            .body("", iterableWithSize(0));
    }

    @Test
    @TestSecurity(user = DomainHelper.EMAIL_USER, roles = {Roles.User})
    void getAttachmentsRegular() {
        assert ticketAttachments.isEmpty();

        ticketAttachments.addAll(List.of(domainHelper.dummyTicketAttachment(ticket, AttachmentType.Created, "Hello World"),
            domainHelper.dummyTicketAttachment(ticket, AttachmentType.Comment, "Hello World")
        ));

        when().get("/ticket/{ticketId}/attachment/", ticket.getId())
            .then()
            .assertThat()
            .statusCode(200)
            .body("", iterableWithSize(2));
    }
}
