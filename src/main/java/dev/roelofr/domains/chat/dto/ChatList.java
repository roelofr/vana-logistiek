package dev.roelofr.domains.chat.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.roelofr.domain.dto.Pagination;

import java.util.List;

@JsonPropertyOrder({"statistics", "chats"})
public record ChatList(
    List<ChatDto> chats,
    Pagination statistics
) {
}
