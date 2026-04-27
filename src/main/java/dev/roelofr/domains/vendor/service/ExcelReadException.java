package dev.roelofr.domains.vendor.service;

import lombok.Getter;

@Getter
public class ExcelReadException extends Exception {
    private final ExcelParser.ExceptionCause causeCode;

    public ExcelReadException(ExcelParser.ExceptionCause causeCode, String message) {
        super(message);
        this.causeCode = causeCode;
    }

    public ExcelReadException(ExcelParser.ExceptionCause causeCode, String message, Exception cause) {
        super(message, cause);
        this.causeCode = causeCode;
    }

}
