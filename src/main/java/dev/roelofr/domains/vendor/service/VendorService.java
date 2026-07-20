package dev.roelofr.domains.vendor.service;

import dev.roelofr.domains.vendor.model.District;
import dev.roelofr.domains.vendor.model.DistrictRepository;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.model.VendorRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class VendorService {
    private final VendorRepository vendorRepository;
    private final DistrictRepository districtRepository;

    public List<Vendor> listVendors() {
        return vendorRepository.listAllSorted();
    }

    public List<Vendor> listInDistrict(District district) {
        return vendorRepository.listInDistrict(district);
    }

    public Optional<Vendor> getVendor(@Nonnull String number) {
        return vendorRepository.findByNumber(number);
    }

    public Optional<Vendor> getVendor(@Nonnull Long id) {
        return vendorRepository.findByIdOptional(id);
    }

    @Transactional
    public Vendor createVendor(@Nonnull String district, @Nonnull String number, @Nonnull String name) {
        var foundDistrict = districtRepository.findByName(district);

        if (foundDistrict.isEmpty()) throw new IllegalArgumentException("District %s not found".formatted(district));

        return createVendor(foundDistrict.get(), number, name);
    }

    @Transactional
    public Vendor createVendor(@Nonnull District district, @Nonnull String number, @Nonnull String name) {
        var vendor = Vendor.builder()
            .district(district)
            .number(number)
            .name(name)
            .build();

        vendorRepository.persist(vendor);

        return vendor;
    }
}
