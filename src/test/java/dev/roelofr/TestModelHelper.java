package dev.roelofr;

import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.VendorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
public class TestModelHelper {
    private final DistrictRepository districtRepository;

    private final VendorRepository vendorRepository;

    public void deleteVendors() {
        vendorRepository.deleteAll();
    }

    public Vendor createVendor(String number, String name, String district) {
        var districtObject = districtRepository.findByName(district).orElseThrow(() -> new IllegalArgumentException("District " + district + " not found"));

        var vendor = Vendor.create(number, name, districtObject);

        vendorRepository.persist(vendor);

        return vendor;
    }
}
