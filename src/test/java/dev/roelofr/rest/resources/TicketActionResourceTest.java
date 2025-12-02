package dev.roelofr.rest.resources;

import dev.roelofr.DomainHelper;
import dev.roelofr.config.Roles;
import dev.roelofr.domain.enums.AttachmentType;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.rest.request.TicketAssignRequest;
import dev.roelofr.rest.request.TicketCommentRequest;
import dev.roelofr.rest.request.TicketResolveRequest;
import dev.roelofr.service.TicketAttachmentService;
import dev.roelofr.service.TicketService;
import dev.roelofr.service.UserService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static dev.roelofr.DomainHelper.EMAIL_NEW;
import static dev.roelofr.DomainHelper.EMAIL_USER;
import static io.restassured.RestAssured.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Slf4j
@QuarkusTest
@Disabled("Uses too many repositories, but everything is TicketService")
@TestHTTPEndpoint(TicketActionResource.class)
@TestSecurity(user = "test@example.com", roles = {Roles.User})
class TicketActionResourceTest {
    final DomainHelper domainHelper = DomainHelper.getInstance();

    @InjectMock
    TicketService ticketService;

    @InjectMock
    TicketAttachmentService attachmentService;

    @InjectMock
    UserService userService;

    Ticket dummyTicket = null;

    @BeforeEach
    void setupDummyTicket() {
        dummyTicket = domainHelper.dummyTicket("Hello World");
        given(ticketService.findById(dummyTicket.getId())).willReturn(Optional.of(dummyTicket));
    }

    @Test
    @TestSecurity(user = EMAIL_NEW)
    void testUnderprivileged() {
        with().pathParam("id", dummyTicket.getId()).when().post("/comment").then().statusCode(401);

        with().pathParam("id", dummyTicket.getId()).when().post("/assign").then().statusCode(401);

        with().pathParam("id", dummyTicket.getId()).when().post("/resolve").then().statusCode(401);
    }

    @Test
    @TestSecurity(user = EMAIL_USER, roles = {Roles.User})
    void comment() {
        assumeTrue(dummyTicket.getStatus() == TicketStatus.Created);

        with()
            .pathParam("id", dummyTicket.getId())
            .contentType(ContentType.JSON)
            .body(new TicketCommentRequest("Hello World"))
            .when()
            .post("/comment")
            .then()
            .statusCode(200);

        verify(attachmentService, times(1))
            .create(same(dummyTicket), any());

        verifyNoMoreInteractions(attachmentService);
        verifyNoInteractions(userService);
    }

    @Test
    @TestSecurity(user = EMAIL_USER, roles = {Roles.User})
    void assign() {
        var dummyUser = domainHelper.randomUser();
        given(userService.findById(dummyUser.getId()))
            .willReturn(Optional.of(dummyUser));

        assumeTrue(dummyTicket.getAssignee() == null);
        assumeTrue(dummyTicket.getStatus() == TicketStatus.Created);

        with()
            .pathParam("id", dummyTicket.getId())
            .contentType(ContentType.JSON)
            .body(new TicketAssignRequest(dummyUser.getId(), null))
            .when()
            .post("/assign")
            .then()
            .statusCode(200);

        verify(ticketService, times(1))
            .assignTo(dummyTicket, dummyUser, null);

        verify(userService, times(1))
            .findById(dummyUser.getId());

        verify(attachmentService, times(1))
            .create(same(dummyTicket), any());

        verifyNoMoreInteractions(attachmentService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @TestSecurity(user = EMAIL_USER, roles = {Roles.User})
    void assignResolved() {
        dummyTicket.setStatus(TicketStatus.Resolved);

        with()
            .pathParam("id", dummyTicket.getId())
            .contentType(ContentType.JSON)
            .body(new TicketAssignRequest(1L, null))
            .when()
            .post("/assign")
            .then()
            .statusCode(415);

        verify(ticketService, times(1)).findById(dummyTicket.getId());
        verifyNoMoreInteractions(ticketService);

        verifyNoInteractions(attachmentService);
        verifyNoInteractions(userService);
    }

    @Test
    @TestSecurity(user = EMAIL_USER, roles = {Roles.User})
    void resolve() {
        var dummyAttachment = TicketAttachment.builder().ticket(dummyTicket).build();

        given(attachmentService.create(dummyTicket, AttachmentType.StatusChange)).willReturn(dummyAttachment);

        assumeTrue(dummyTicket.getStatus() == TicketStatus.Created);
        assumeTrue(dummyTicket.getCompletedAt() == null);

        with()
            .pathParam("id", dummyTicket.getId())
            .contentType(ContentType.JSON)
            .body(new TicketResolveRequest(null))
            .when()
            .post("/resolve")
            .then()
            .statusCode(200);

        assertEquals(TicketStatus.Resolved, dummyTicket.getStatus());
        assertNotNull(dummyTicket.getCompletedAt());

        verify(attachmentService, times(1))
            .create(same(dummyTicket), any());

        verifyNoMoreInteractions(attachmentService);
        verifyNoInteractions(userService);
    }

    @Test
    @TestSecurity(user = EMAIL_USER, roles = {Roles.User})
    void resolveResolved() {
        dummyTicket.setStatus(TicketStatus.Resolved);

        with()
            .pathParam("id", dummyTicket.getId())
            .contentType(ContentType.JSON)
            .body(new TicketResolveRequest(null))
            .when()
            .post("/resolve")
            .then()
            .statusCode(415);

        verify(ticketService, times(1)).findById(dummyTicket.getId());
        verifyNoMoreInteractions(ticketService);

        verifyNoInteractions(attachmentService);
        verifyNoInteractions(userService);
    }
}
