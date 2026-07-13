package dev.roelofr.domains.chat.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemMessageTypeTest {

    @ParameterizedTest
    @EnumSource(SystemMessageType.class)
    void value(SystemMessageType type) {
        var expectedValue = type.name().replaceAll("(?<=[a-z])([A-Z])", "-$1").toLowerCase();
        assertEquals(expectedValue, type.value());
    }
}
