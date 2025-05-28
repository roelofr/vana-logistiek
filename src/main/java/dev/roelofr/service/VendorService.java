package dev.roelofr.service;

import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.VendorRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jboss.resteasy.reactive.ResponseStatus;

@ApplicationScoped
@AllArgsConstructor(onConstructor = @__(@Inject))
public class VendorService {
    final VendorRepository vendorRepository;
    private final DistrictRepository districtRepository;

    public @Nullable Vendor getVendor(@Nonnull Long id) {
        return vendorRepository.findById(id);
    }

    @Transactional
    public Vendor createVendor(@Nonnull String district, @Nonnull String number, @Nonnull String name) {
        var foundDistrict = districtRepository.findByName(district);

        if (foundDistrict.isEmpty())
            throw new IllegalArgumentException("District %s not found".formatted(district));

        var vendor = Vendor.builder()
            .district(foundDistrict.get())
            .number(number)
            .name(name)
            .build();

        vendorRepository.persist(vendor);

        return vendor;
    }
}
