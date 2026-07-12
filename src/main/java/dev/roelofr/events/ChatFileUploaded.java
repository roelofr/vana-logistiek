package dev.roelofr.events;

import dev.roelofr.domains.chat.model.ChatEntry;
import lombok.Builder;

@Builder
public record ChatFileUploaded(
    ChatEntry file
) {
    public long id() {
        return file.getId();
    }
}
