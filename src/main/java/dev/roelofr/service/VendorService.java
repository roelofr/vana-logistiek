package dev.roelofr.service;

import dev.roelofr.domain.User;
import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.VendorRepository;
import dev.roelofr.service.vendor.ExcelParser;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.roelofr.Constants.LocaleDutch;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor = @__(@Inject))
public class VendorService {
    private final VendorRepository vendorRepository;
    private final DistrictRepository districtRepository;

    public List<Vendor> listVendors() {
        return vendorRepository.listAllSorted();
    }

    public List<Vendor> listVendors(@Nullable User user) {
        if (user != null && user.getTeam() != null)
            return vendorRepository.listAllSortedWithPreferentialTeam(user.getTeam());

        return listVendors();
    }

    public Optional<Vendor> getVendor(@Nonnull String id) {
        return vendorRepository.findByNumber(id);
    }

    public Optional<Vendor> getVendor(@Nonnull Long id) {
        return vendorRepository.findByIdOptional(id);
    }

    @Transactional
    public Vendor createVendor(@Nonnull String district, @Nonnull String number, @Nonnull String name) {
        var foundDistrict = districtRepository.findByName(district);

        if (foundDistrict.isEmpty()) throw new IllegalArgumentException("District %s not found".formatted(district));

        var vendor = Vendor.builder()
            .district(foundDistrict.get())
            .number(number)
            .name(name)
            .build();

        vendorRepository.persist(vendor);

        return vendor;
    }

    @Transactional
    public List<Vendor> importVendorList(File excelVendorFile) {
        var districts = districtRepository.listAll();
        List<Vendor> vendors;

        try {
            var reader = new ExcelParser(excelVendorFile);

            reader.verifyFile();

            reader.readFile();

            var headers = reader.mapHeaders();

            log.info("Headers are {}", headers);

            vendors = reader.mapToVendor(districts);
        } catch (ExcelParser.ExcelReadException e) {
            log.error("Failed to convert XLSX to list of vendors, caught {}: {}", e.getClass().getSimpleName(), e.getMessage());

            if (e.getCauseCode() == ExcelParser.ExceptionCause.User)
                throw new BadRequestException(e.getMessage(), e);

            if (e.getCauseCode() == ExcelParser.ExceptionCause.Logic)
                log.error("Logic error! {}", e.getMessage(), e);

            throw new InternalServerErrorException(e.getMessage(), e);
        }

        // Ensure none of the to-add vendors exist
        var existingVendorNumbers = vendorRepository.streamAll().map(this::mapVendorToCleanNumber).collect(Collectors.toSet());
        var hasOverlap = vendors.stream().map(this::mapVendorToCleanNumber).anyMatch(existingVendorNumbers::contains);

        if (hasOverlap) {
            throw new BadRequestException("There was overlap between the new and existing vendor numbers!");
        }

        vendorRepository.persist(vendors);

        return vendors;
    }

    private String mapVendorToCleanNumber(Vendor vendor) {
        return vendor.getNumber().trim().toLowerCase(LocaleDutch);
    }
}
