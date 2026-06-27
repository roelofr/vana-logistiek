package dev.roelofr.domains.vendor.service;

import dev.roelofr.domains.vendor.model.DistrictRepository;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.model.VendorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static dev.roelofr.Constants.LocaleDutch;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class VendorAdminService {
    private final VendorRepository vendorRepository;
    private final DistrictRepository districtRepository;
    private final ExcelParser excelParser;

    @Transactional
    public List<Vendor> importVendorList(File excelVendorFile) {
        var districts = districtRepository.listAll();
        List<Vendor> vendors = new ArrayList<>();

        try {
            excelParser.setFile(excelVendorFile);

            excelParser.verifyFile();

            log.info("File seems to be valid!");

            var sheets = excelParser.readFile();

            log.info("Read {} sheets", sheets.size());

            for (XSSFSheet sheet : sheets) {
                try {
                    log.info("Reading sheet [{}]...", sheet.getSheetName());

                    var headers = excelParser.mapHeaders(sheet);

                    log.info("Headers are {}", headers);

                    vendors.addAll(excelParser.mapToVendor(sheet, districts));
                } catch (ExcelReadException e) {
                    log.warn("Rejected sheet {}", sheet.getSheetName());
                }
            }

            log.info("Read finished");
        } catch (ExcelReadException e) {
            log.error("Failed to convert XLSX to list of vendors, caught {}: {}", e.getClass().getSimpleName(), e.getMessage());

            if (e.getCauseCode() == ExcelParser.ExceptionCause.User)
                throw new BadRequestException(e.getMessage(), e);

            if (e.getCauseCode() == ExcelParser.ExceptionCause.Logic)
                log.error("Logic error! {}", e.getMessage(), e);

            throw new InternalServerErrorException(e.getMessage(), e);
        }

        vendorRepository.persist(vendors);

        vendors.forEach(vendor -> log.info("New vendor: {} ({})", vendor.getName(), vendor.getNumber()));

        return vendors;
    }

    private String mapVendorToCleanNumber(Vendor vendor) {
        return vendor.getNumber().trim().toLowerCase(LocaleDutch);
    }
}
