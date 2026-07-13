package dev.roelofr.domains.users.jpa;

import dev.roelofr.domains.users.model.UserFlags;
import io.quarkus.runtime.util.StringUtil;
import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserFlagsConverter implements AttributeConverter<List<UserFlags>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<UserFlags> enums) {
        if (enums == null || enums.isEmpty())
            return "";
        return enums.stream().map(UserFlags::name).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<UserFlags> convertToEntityAttribute(String string) {
        if (StringUtil.isNullOrEmpty(string))
            return new ArrayList<>();

        var mapped = Arrays.stream(string.split(SPLIT_CHAR))
            .map(UserFlags::valueOf)
            .toList();

        return new ArrayList<>(mapped);
    }
}
