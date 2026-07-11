package dev.roelofr.domains.vendor.service;

import dev.roelofr.domains.vendor.model.District;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.service.FileService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static dev.roelofr.Constants.LocaleDutch;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ExcelParser {
    private final List<String> EXCEL_MIME_TYPES = List.of("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private final FileService fileService;

    private File source = null;

    private XSSFSheet sheet = null;
    private Integer headerRowIndex = null;
    private Map<WantedRow, Integer> cellMapping;

    public void setFile(File file) {
        source = file;
    }

    private void requiresFile() throws ExcelReadException {
        if (source == null) throw new ExcelReadException(ExceptionCause.Logic, "Sheet source file was unset.");
    }

    private String determineMime() throws ExcelReadException {
        requiresFile();
        return fileService.getFileMime(source);
    }

    public void verifyFile() throws ExcelReadException {
        requiresFile();

        String mimeType = determineMime();

        if (mimeType != null && EXCEL_MIME_TYPES.contains(mimeType.toLowerCase()))
            return;

        if (mimeType == null) {
            log.warn("Cannot determine mime. Winging it.");
            return;
        }

        log.info("mime of [{}] is [{}]", source.getName(), mimeType);
        throw new ExcelReadException(ExceptionCause.User, "This does not seem to be an Excel file.");
    }

    public ArrayList<XSSFSheet> readFile() throws ExcelReadException {
        requiresFile();

        log.info("Loading Excel file [{}]...", source.getName());

        try {
            var inputStream = new FileInputStream(source);
            log.info("Opened stream");

            var workbook = new XSSFWorkbook(inputStream);
            log.info("Opened workbook {}", workbook);

            int totalSheetCount = workbook.getNumberOfSheets();
            var sheets = new ArrayList<XSSFSheet>(totalSheetCount);

            for (var index = 0; index < totalSheetCount; index++) {
                if (workbook.getSheetVisibility(index) != SheetVisibility.VISIBLE)
                    continue;

                log.info("Adding sheet {}: {}", index, workbook.getSheetName(index));

                sheets.add(workbook.getSheetAt(index));
            }

            return sheets;
        } catch (IOException exception) {
            log.warn("Failed to read Excel file {}: {}", source.getName(), exception.getMessage(), exception);

            throw new ExcelReadException(ExceptionCause.User, "Inlezen van Excel-bestand mislukt: %s.".formatted(exception.getMessage()), exception);
        }
    }

    public Map<WantedRow, Integer> mapHeaders(XSSFSheet sheet) throws ExcelReadException {
        XSSFRow headerRow;
        var rowTrack = new AtomicInteger(0);
        do {
            if (rowTrack.get() > 10)
                throw new ExcelReadException(ExceptionCause.User, "Failed to find a proper header after 10 rows.");

            headerRow = sheet.getRow(rowTrack.getAndIncrement());
        } while (headerRow != null && headerRow.getPhysicalNumberOfCells() < 2); // Expect name and number, at least

        if (headerRow == null)
            throw new ExcelReadException(ExceptionCause.User, "Kop van bestand lijkt ongeldig.");

        headerRowIndex = headerRow.getRowNum();

        log.info("Loaded excel");

        var result = new HashMap<WantedRow, Integer>();
        headerRow.cellIterator().forEachRemaining(cell -> {
            // Handle numeric cells
            String cellText = switch (cell.getCellType()) {
                case NUMERIC -> Integer.toString((int) Math.floor(cell.getNumericCellValue()), 10);
                case STRING -> cell.getStringCellValue();
                default -> "";
            };

            // Ensure consistent format
            cellText = cellText.toLowerCase().trim();

            if (cellText.contains("standhouder") || cellText.contains("naam") || cellText.contains("cater") || cellText.contains("name")) {
                log.info("Found Name in cell {}: {}", cell.getAddress().formatAsString(), cellText);
                result.put(WantedRow.Name, cell.getColumnIndex());
            } else if (cellText.contains("nummer") || cellText.contains("numbre")) {
                log.info("Found Number in cell {}: {}", cell.getAddress().formatAsString(), cellText);
                result.put(WantedRow.Number, cell.getColumnIndex());
            } else if (cellText.contains("toevoeging") || cellText.contains("voegsel")) {
                log.info("Found Suffix in cell {}: {}", cell.getAddress().formatAsString(), cellText);
                result.put(WantedRow.Suffix, cell.getColumnIndex());
            } else if (cellText.contains("team") || cellText.contains("wijk") || cellText.contains("kleur") || cellText.contains("district")) {
                log.info("Found District in cell {}: {}", cell.getAddress().formatAsString(), cellText);
                result.put(WantedRow.District, cell.getColumnIndex());
            }
        });

        if (result.containsKey(WantedRow.Name) && result.containsKey(WantedRow.Number))
            return cellMapping = result;

        throw new ExcelReadException(ExceptionCause.User, "Kan een of meer kolommen niet vinden in het bestand.");
    }

    public List<Vendor> mapToVendor(XSSFSheet sheet, List<District> districts) {
        final var emptyRowCount = new AtomicInteger(0);
        final var currentRow = new AtomicInteger(headerRowIndex + 1);

        final var vendors = new LinkedList<Vendor>();
        final var mappedDistricts = districts.stream().collect(
            Collectors.toMap(
                district -> district.getName().toLowerCase(),
                district -> district
            )
        );

        var defaultDistrict = districts.stream()
            .filter(d -> d.getName().equalsIgnoreCase("default"))
            .findFirst()
            .orElse(districts.getFirst());

        final var nameCellIndex = cellMapping.get(WantedRow.Name);
        final var numberCellIndex = cellMapping.get(WantedRow.Number);
        final var suffixCellIndex = cellMapping.get(WantedRow.Suffix);
        final var districtCellIndex = cellMapping.get(WantedRow.District);

        do {
            var row = sheet.getRow(currentRow.getAndIncrement());

            // Empty rows might show up as null
            if (row == null) {
                emptyRowCount.incrementAndGet();
                continue;
            }

            var nameCell = row.getCell(nameCellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            var numberCell = row.getCell(numberCellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            var suffixCell = (suffixCellIndex != null) ? row.getCell(suffixCellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) : null;

            if (nameCell == null || numberCell == null) {
                log.info("Row [{}} is EMPTY", row.getRowNum());
                emptyRowCount.incrementAndGet();
                continue;
            }

            if (nameCell.getCellType() == CellType.NUMERIC) {
                log.info("Row [{}} has non-string Name cell {}?", row.getRowNum(), nameCell.getAddress().formatAsString());
                emptyRowCount.incrementAndGet();
                continue;
            }

            var nameValue = nameCell.getStringCellValue();
            var numberValue = numberCell.getCellType() == CellType.NUMERIC ? readNumeric(numberCell) : numberCell.getStringCellValue();
            if (nameValue.isBlank() || numberValue.isBlank()) {
                log.info("Row [{}} is BLANK", row.getRowNum());
                emptyRowCount.incrementAndGet();
                continue;
            }

            // Try and resolve a suffix, if the column is present
            var vendorNumber = getVendorNumber(suffixCell, numberValue);

            // Reset empty row tracker
            emptyRowCount.set(0);

            // Make vendor and add to list
            var vendor = Vendor.builder()
                .name(nameValue.trim())
                .number(vendorNumber)
                .build();

            vendors.push(vendor);

            log.info("Row [{}] is VENDOR({}, {})", row.getRowNum(), vendor.getNumber(), vendor.getName());

            // Add to default district for now
            vendor.setDistrict(defaultDistrict);

            // Run team check
            if (districtCellIndex == null)
                continue;

            var districtCell = row.getCell(districtCellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (districtCell == null) {
                log.info("District column is present, but value is null for {}", vendor.getNumber());
                continue;
            }

            var districtValue = districtCell.getStringCellValue();
            if (districtValue.trim().isBlank()) {
                log.info("District column is present, but value is empty for {}", vendor.getNumber());
                continue;
            }

            var district = mappedDistricts.get(districtValue.toLowerCase().trim());
            if (district == null) {
                log.warn("District column is present, but value {} of {} does not map", districtValue, vendor.getNumber());
                continue;
            }

            vendor.setDistrict(district);
            log.info("Vendor {} assigned to district {}", vendor.getNumber(), district.getName());
        } while (emptyRowCount.get() < 10);

        return vendors;
    }

    private String readNumeric(XSSFCell numberCell) {
        var numberValue = numberCell.getNumericCellValue();

        // Check if number has decimals, then print as '1.2'
        var hasDecimals = Double.compare(Math.floor(numberValue), Math.ceil(numberValue)) != 0;
        if (hasDecimals)
            return String.format("%.1f", numberValue);

        // Else round the number to an integer
        return String.format("%.0f", numberValue);
    }

    private static @NonNull String getVendorNumber(XSSFCell suffixCell, String numberValue) {
        String suffixValue = "";
        if (suffixCell != null) {
            suffixValue = (suffixCell.getCellType() == CellType.NUMERIC ? suffixCell.getRawValue() : suffixCell.getStringCellValue())
                .toLowerCase(LocaleDutch)
                .replaceAll("[^a-z0-9]", "")
                .trim();
        }

        // Prep number and trim it if oversize.
        var vendorNumber = (numberValue.trim() + suffixValue);
        if (vendorNumber.length() > 10)
            vendorNumber = vendorNumber.substring(0, 10);
        return vendorNumber;
    }

    public enum ExceptionCause {
        Logic,
        User,
        System
    }

    public enum WantedRow {
        Name,
        Number,
        Suffix,
        District
    }

}
