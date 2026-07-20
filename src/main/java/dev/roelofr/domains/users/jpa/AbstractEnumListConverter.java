package dev.roelofr.domains.users.jpa;

import io.quarkus.runtime.util.StringUtil;
import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractEnumListConverter<T extends Enum<T>> implements AttributeConverter<List<T>, String> {
    private static final String SPLIT_CHAR = ";";

    protected abstract Class<T> getEnumClass();

    @Override
    public String convertToDatabaseColumn(List<T> attribute) {
        if (attribute == null) return "";

        return attribute.stream()
            .map(Enum::name)
            .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<T> convertToEntityAttribute(String dbData) {
        if (StringUtil.isNullOrEmpty(dbData))
            return new ArrayList<>();

        return Arrays.stream(dbData.split(SPLIT_CHAR))
            .map(name -> Enum.valueOf(getEnumClass(), name.trim()))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}

