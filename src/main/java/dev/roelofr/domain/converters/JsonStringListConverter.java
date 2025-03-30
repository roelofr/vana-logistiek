package dev.roelofr.domain.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@Converter(autoApply = true)
public class JsonStringListConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }

        var cleanList = attribute.stream()
            .filter(value -> value != null && !value.isBlank())
            .sorted()
            .toList();

        try {
            return objectMapper.writeValueAsString(cleanList);
        } catch (JsonProcessingException e) {
            log.warn("List to JSON conversion failed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.equals("null"))
            return null;

        log.info("Converting value [{}]...", dbData);

        if (dbData.isEmpty())
            return List.of();

        try {
            // Data is double-encoded, decode first
            if (dbData.charAt(0) == '"' && dbData.charAt(dbData.length() - 1) == '"') {
                log.warn("Received double-encoded JSON array!");
                dbData = objectMapper.readValue(dbData, String.class);
            }

            return objectMapper.readerForListOf(String.class).readValue(dbData);
        } catch (JsonProcessingException ex) {
            log.error("Failed to process JSON value {}: {}", dbData, ex.getMessage(), ex);
            return null;
        }
    }
}
