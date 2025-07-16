package dev.roelofr.service.vendor;

import dev.roelofr.domain.District;
import dev.roelofr.domain.Vendor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static dev.roelofr.Constants.LocaleDutch;

@Slf4j
@RequiredArgsConstructor
public class ExcelParser {
    private final String MIME_EXCEL_FILE = "application/vnd.ms-excel";

    private final File source;
    private XSSFSheet sheet = null;
    private Integer headerRowIndex = null;
    private Map<WantedRow, Integer> cellMapping;

    private String determineMime() throws ExcelReadException {
        try {
            var probeMime = Files.probeContentType(source.toPath());
            if (probeMime != null)
                return probeMime.toLowerCase();

            var connection = source.toURI().toURL().openConnection();
            var connMime = connection.getContentType();
            if (connMime != null && !connMime.equalsIgnoreCase("content/unknown"))
                return connMime.toLowerCase();

            return null;
        } catch (IOException e) {
            throw new ExcelReadException(ExceptionCause.System, "Failed to read file");
        }
    }

    public void verifyFile() throws ExcelReadException {
        String mimeType = determineMime();

        if (MIME_EXCEL_FILE.equalsIgnoreCase(mimeType))
            return;

        if (mimeType == null) {
            log.warn("Cannot determine mime. Winging it.");
            return;
        }

        log.info("mime of [{}] is [{}]", source.getName(), mimeType);
        throw new ExcelReadException(ExceptionCause.User, "This does not seem to be an Excel file.");
    }

    public void readFile() throws ExcelReadException {
        if (source == null)
            throw new ExcelReadException(ExceptionCause.Logic, "Sheet source file was unset.");

        log.info("Loading Excel file [{}]...", source.getName());

        try {
            var inputStream = new FileInputStream(source);
            log.info("Opened stream");

            var workbook = new XSSFWorkbook(inputStream);
            log.info("Opened workbook {}", workbook);

            sheet = workbook.getSheetAt(0);
            log.info("Opened Sheet 0 {}", sheet.toString());
        } catch (IOException exception) {
            log.warn("Failed to read Excel file {}: {}", source.getName(), exception.getMessage(), exception);

            throw new ExcelReadException(ExceptionCause.User, "Failed to read Excel sheet: %s.".formatted(exception.getMessage()), exception);
        }
    }

    public Map<WantedRow, Integer> mapHeaders() throws ExcelReadException {
        if (sheet == null)
            throw new ExcelReadException(ExceptionCause.Logic, "Sheet was not yet read.");

        XSSFRow headerRow;
        var rowTrack = new AtomicInteger(0);
        do {
            if (rowTrack.get() > 10)
                throw new ExcelReadException(ExceptionCause.User, "Failed to find a proper header after 10 rows.");

            headerRow = sheet.getRow(rowTrack.getAndIncrement());
        } while (headerRow.getPhysicalNumberOfCells() < 2); // Expect name and number, at least

        headerRowIndex = headerRow.getRowNum();

        log.info("Loaded excel");

        var result = new HashMap<WantedRow, Integer>();
        headerRow.cellIterator().forEachRemaining(cell -> {
            var cellText = cell.getStringCellValue().toLowerCase().trim();

            if (cellText.contains("standhouder") || cellText.contains("naam") || cellText.contains("name"))
                result.put(WantedRow.Name, cell.getColumnIndex());
            else if (cellText.contains("nummer") || cellText.contains("numbre"))
                result.put(WantedRow.Number, cell.getColumnIndex());
            else if (cellText.contains("toevoeging") || cellText.contains("voegsel"))
                result.put(WantedRow.Suffix, cell.getColumnIndex());
            else if (cellText.contains("district") || cellText.contains("wijk") || cellText.contains("kleur"))
                result.put(WantedRow.District, cell.getColumnIndex());
        });

        if (result.containsKey(WantedRow.Name) && result.containsKey(WantedRow.Number))
            return cellMapping = result;

        throw new ExcelReadException(ExceptionCause.User, "Kan een of meer kolommen niet vinden in het bestand.");
    }

    public List<Vendor> mapToVendor(List<District> districts) {
        final var emptyRowCount = new AtomicInteger(0);
        final var currentRow = new AtomicInteger(headerRowIndex + 1);

        final var vendors = new LinkedList<Vendor>();
        final var mappedDistricts = districts.stream()
            .collect(Collectors.toMap(district -> district.getName().toLowerCase(), district -> district));

        final var nameCellIndex = cellMapping.get(WantedRow.Name);
        final var numberCellIndex = cellMapping.get(WantedRow.Number);
        final var suffixCellIndex = cellMapping.get(WantedRow.Suffix);
        final var districtCellIndex = cellMapping.get(WantedRow.District);

        do {
            var row = sheet.getRow(currentRow.getAndIncrement());
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
            var numberValue = numberCell.getCellType() == CellType.NUMERIC ? numberCell.getRawValue() : numberCell.getStringCellValue();
            if (nameValue.isBlank() || numberValue.isBlank()) {
                log.info("Row [{}} is BLANK", row.getRowNum());
                emptyRowCount.incrementAndGet();
                continue;
            }

            // Try and resolve a suffix, if the column is present
            String suffixValue = "";
            if (suffixCell != null) {
                suffixValue = (suffixCell.getCellType() == CellType.NUMERIC ? suffixCell.getRawValue() : suffixCell.getStringCellValue())
                    .toLowerCase(LocaleDutch)
                    .replaceAll("[^a-z0-9]", "[^a-z0-9]")
                    .trim();
            }

            // Reset empty row tracker
            emptyRowCount.set(0);

            // Make vendor and add to list
            var vendor = Vendor.builder()
                .name(nameValue.trim())
                .number(numberValue.trim() + suffixValue)
                .build();

            vendors.push(vendor);

            log.info("Row [{}] is VENDOR({}, {})", row.getRowNum(), vendor.getNumber(), vendor.getName());

            // Run district check
            if (districtCellIndex == null)
                continue;

            var districtCell = row.getCell(districtCellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (districtCell == null)
                continue;

            var districtValue = districtCell.getStringCellValue();
            if (districtValue.trim().isBlank())
                continue;

            var district = mappedDistricts.get(districtValue.toLowerCase().trim());
            if (district == null)
                continue;

            vendor.setDistrict(district);
            log.info(" ".repeat("Row [{}] is ".formatted(row.getRowNum()).length()) + "with district {}", district.getName());
        } while (emptyRowCount.get() < 10);

        return vendors;
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

    @Getter
    public static class ExcelReadException extends Exception {
        private final ExceptionCause causeCode;

        public ExcelReadException(ExceptionCause causeCode, String message) {
            super(message);
            this.causeCode = causeCode;
        }

        public ExcelReadException(ExceptionCause causeCode, String message, Exception cause) {
            super(message, cause);
            this.causeCode = causeCode;
        }

    }
}
