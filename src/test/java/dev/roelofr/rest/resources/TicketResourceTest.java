package dev.roelofr.rest.resources;

import dev.roelofr.MockDomainHelper;
import dev.roelofr.config.Roles;
import dev.roelofr.domain.enums.TicketStatus;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.TicketRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.BDDMockito.given;

@QuarkusTest
@TestHTTPEndpoint(TicketResource.class)
class TicketResourceTest {
    @InjectMock
    TicketRepository ticketRepository;

    @InjectMock
    DistrictRepository districtRepository;

    MockDomainHelper domainHelper = new MockDomainHelper();

    @Test
    @TestSecurity(user = "")
    void testUnauthenticated() {
        when().get("/")
            .then()
            .onFailMessage("list")
            .statusCode(401);

        when().get("/by-district/test")
            .then()
            .onFailMessage("listForDistrict")
            .statusCode(401);

        when().get("/by-user")
            .then()
            .onFailMessage("listForUser")
            .statusCode(401);

        when().get("/1")
            .then()
            .onFailMessage("find")
            .statusCode(401);

        when().post("/")
            .then()
            .onFailMessage("create")
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "test", roles = {Roles.User})
    void list() {
        final var ticketOne = domainHelper.dummyTicket("Test List One");
        ticketOne.setStatus(TicketStatus.Resolved);
        ticketOne.setCompletedAt(LocalDateTime.now());

        final var ticketTwo = domainHelper.dummyTicket("Test List Two");
        ticketTwo.setStatus(TicketStatus.Assigned);
        ticketTwo.setAssignee(domainHelper.randomUser());

        final var ticketThree = domainHelper.dummyTicket("Test List Three");

        given(ticketRepository.streamAll())
            .willReturn(Stream.of(
                ticketOne,
                ticketTwo,
                ticketThree
            ));

        when().get("/")
            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(200)

            .body(".", iterableWithSize(3))

            .body("[0].description", equalTo(ticketOne.getDescription()))
            .body("[1].description", equalTo(ticketTwo.getDescription()))
            .body("[2].description", equalTo(ticketThree.getDescription()));
    }

    @Test
    @TestSecurity(user = "test", roles = {Roles.User})
    void listForDistrict() {
        final var district = domainHelper.randomDistrict();
        final var ticket = domainHelper.dummyTicket("Test List Three");

        given(districtRepository.findByName(district.getName()))
            .willReturn(Optional.of(district));

        given(ticketRepository.findForDistrict(district))
            .willReturn(List.of(ticket));

        when().get("/by-district/" + district.getName())
            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(200)
            .body("", iterableWithSize(1))
            .body("[0].description", equalTo(ticket.getDescription()));
    }

    @Test
    @TestSecurity(user = "test", roles = {Roles.User})
    void listForDistrictNotFound() {
        final String TEST_NAME = "test-not-found";

        given(districtRepository.findByName(TEST_NAME))
            .willReturn(Optional.empty());

        when().get("/by-district/" + TEST_NAME)
            .then()
            .log().ifValidationFails()

            .assertThat()
            .statusCode(404);
    }

    @Test
    void find() {
    }

    @Test
    void create() {
    }
}
