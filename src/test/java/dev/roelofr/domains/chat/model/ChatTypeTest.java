package dev.roelofr.domains.chat.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatTypeTest {

    @Test
    void value() {
        assertEquals("regular", ChatType.Regular.value());
        assertEquals("group", ChatType.Group.value());
        assertEquals("issue", ChatType.Issue.value());
    }
}
