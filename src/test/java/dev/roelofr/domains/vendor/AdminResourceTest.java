package dev.roelofr.domains.vendor;

import dev.roelofr.Roles;
import dev.roelofr.domains.vendor.model.VendorRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestHTTPEndpoint(AdminResource.class)
class AdminResourceTest {
    VendorRepository vendorRepository;

    private static final List<VendorSpec> validVendors = List.of(
        // Catering
        new VendorSpec("C1", "Cater-Aah", "Rood"),
        new VendorSpec("C2", "Beeter Burgers", "Rood"),
        new VendorSpec("C3", "Catering!", "Groen"),
        new VendorSpec("C4", "Dirk’s Vega Sticks", "Groen"),
        new VendorSpec("C6", "Eten en Drinken", "Oranje"),
        new VendorSpec("C7", "Fida’s Koffie", "Oranje"),

        // Vendors
        new VendorSpec("101", "Koetjes", "Groen"),
        new VendorSpec("102", "Kalfjes", "Groen"),
        new VendorSpec("103", "Bezige Bijtjes", "Groen"),
        new VendorSpec("104", "Heidense Hekjes", "Groen"),
        new VendorSpec("110", "Harrie’s Harembroeken Handel", "Groen"),
        new VendorSpec("110a", "Harrie’s Side Hussel", "Groen"),
        new VendorSpec("115", "Gerrie", "Groen"),
        new VendorSpec("115b", "Gerrie’s Dranktent", "Groen"),
        new VendorSpec("Achter 115", "Gerrie’s Bedje", "Groen"),

        new VendorSpec("201", "Gothic Swords", "Oranje"),
        new VendorSpec("202", "Gothic Sabre", "Oranje"),
        new VendorSpec("203", "Gothic Stabbies", "Oranje"),
        new VendorSpec("204", "Gothic Weapons", "Oranje"),
        new VendorSpec("205", "Gothic Essentials (mostly weapons)", "Oranje"),

        new VendorSpec("301a", "Evert’s Boeken", "Rood"),
        new VendorSpec("301b", "Edwin’s Boeken", "Rood"),
        new VendorSpec("301c", "Elisa’s Boeken", "Rood"),
        new VendorSpec("301d", "Eliza Reads", "Rood"),
        new VendorSpec("302", "Ellis’ Book Shop", "Rood"),
        new VendorSpec("303a", "Ellis’ Leater Craft", "Rood"),
        new VendorSpec("303b", "Leather And More", "Rood")
    );

    @Test
    @TestSecurity(user = "admin", roles = {Roles.Admin})
    void importVendorList() {
        RestAssured.given()
            .multiPart("file", new File("src/test/resources/vendors.json"))
            .when()
            .post("/import")
            .then()
            .statusCode(200);

        QuarkusTransaction.requiringNew().run(() ->
            validVendors.forEach(vendor -> {
                var result = vendorRepository.findByNumber(vendor.number);
                assertTrue(result.isPresent(), () -> String.format("Expected a vendor with number %s to exist", vendor.number));

                var resultVendor = result.get();
                assertEquals(vendor.name, resultVendor.getName(), () -> String.format(
                    "Expected vendor %s to have name %s, but got %s instead",
                    vendor.number,
                    vendor.name,
                    resultVendor.getName()
                ));

                assertTrue(
                    resultVendor.getDistrict().getName().toLowerCase().contains(vendor.district.toLowerCase()), () -> String.format(
                        "Expected vendor %s to be in a district with name %s, but got %s instead",
                        vendor.number,
                        vendor.district,
                        resultVendor.getDistrict().getName()
                    ));
            })
        );
    }

    record VendorSpec(String number, String name, String district) {
        //
    }
}
