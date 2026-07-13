package dev.roelofr.domains.chat.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatStateTest {

    @Test
    void value() {
        assertEquals("active", ChatState.Active.value());
        assertEquals("permanent", ChatState.Permanent.value());
        assertEquals("closed", ChatState.Closed.value());
    }
}
