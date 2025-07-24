package dev.roelofr.domain.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@Converter(autoApply = true)
public class JsonHashMapConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper objectMapper;

    boolean isNotEmpty(Map.Entry<String, Object> entry) {
       var value = entry.getValue();
       if (value == null)
           return false;

       if (value instanceof String valueStr)
           return ! valueStr.isBlank();

       return true;
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }

        var cleanMap = attribute.entrySet()
            .stream()
            .filter(this::isNotEmpty)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        try {
            return objectMapper.writeValueAsString(cleanMap);
        } catch (JsonProcessingException e) {
            log.warn("List to JSON conversion failed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.equals("null"))
            return null;

        if (dbData.isEmpty())
            return new HashMap<>();

        try {
            // Data is double-encoded, decode first
            if (dbData.charAt(0) == '"' && dbData.charAt(dbData.length() - 1) == '"') {
                log.warn("Received double-encoded JSON array!");
                dbData = objectMapper.readValue(dbData, String.class);
            }

            return objectMapper.readerForMapOf(Object.class).readValue(dbData);
        } catch (JsonProcessingException ex) {
            log.error("Failed to process JSON value {}: {}", dbData, ex.getMessage(), ex);
            return null;
        }
    }
}
