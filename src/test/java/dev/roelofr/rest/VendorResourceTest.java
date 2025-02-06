package dev.roelofr.rest;

import dev.roelofr.TestModelHelper;
import dev.roelofr.repository.VendorRepository;
import dev.roelofr.rest.resources.VendorResource;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(VendorResource.class)
public class VendorResourceTest {
    @Inject
    TestModelHelper testModelHelper;

    @Inject
    VendorRepository vendorRepository;

    @Test
    @TestTransaction
    public void testListVendor() {
        testModelHelper.deleteVendors();

        testModelHelper.createVendor("100a", "Test One", "rood");

        testModelHelper.createVendor("1100b", "Test Two", "zandbruin");

        assertEquals(2, vendorRepository.count());

        when().get()

            .then()
            .statusCode(200)
            .log().ifValidationFails()

            .body("[0].name", is("Test One"))
            .body("[0].number", is("100a"))

            .body("[1].name", is("Test Two"))
            .body("[1].number", is("1100a"));
    }
}
