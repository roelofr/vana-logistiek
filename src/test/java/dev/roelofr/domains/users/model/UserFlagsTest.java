package dev.roelofr.domains.users.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFlagsTest {

    @Test
    void value() {
        assertEquals("onboarded", UserFlags.Onboarded.value());
    }
}
