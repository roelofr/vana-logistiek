package dev.roelofr.domains.vendor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VendorTestData {
    public static final List<VendorSpec> validXlsxFileVendors = List.of(
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

    public record VendorSpec(String number, String name, String district) {
        //
    }
}
