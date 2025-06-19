package dev.roelofr.service;

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

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor = @__(@Inject))
public class VendorService {
    private final VendorRepository vendorRepository;
    private final DistrictRepository districtRepository;

    public List<Vendor> listVendors() {
        return vendorRepository.listAllSorted();
    }

    public @Nullable Vendor getVendor(@Nonnull Long id) {
        return vendorRepository.findById(id);
    }

    @Transactional
    public Vendor createVendor(@Nonnull String district, @Nonnull String number, @Nonnull String name) {
        var foundDistrict = districtRepository.findByName(district);

        if (foundDistrict.isEmpty()) throw new IllegalArgumentException("District %s not found".formatted(district));

        var vendor = Vendor.builder().district(foundDistrict.get()).number(number).name(name).build();

        vendorRepository.persist(vendor);

        return vendor;
    }

    @Transactional
    public List<Vendor> importVendorList(File excelVendorFile) {
        var districts = districtRepository.listAll();

        try {
            var reader = new ExcelParser(excelVendorFile);

            reader.verifyFile();

            reader.readFile();

            var headers = reader.mapHeaders();

            log.info("Headers are {}", headers);

            var vendors = reader.mapToVendor(districts);

            vendorRepository.persist(vendors);

            return vendors;
        } catch (ExcelParser.ExcelReadException e) {
            log.error("Failed to convert XLSX to list of vendors, caught {}: {}", e.getClass().getSimpleName(), e.getMessage());

            if (e.getCauseCode() == ExcelParser.ExceptionCause.User)
                throw new BadRequestException(e.getMessage(), e);

            if (e.getCauseCode() == ExcelParser.ExceptionCause.Logic)
                log.error("Logic error! {}", e.getMessage(), e);

            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }
}
