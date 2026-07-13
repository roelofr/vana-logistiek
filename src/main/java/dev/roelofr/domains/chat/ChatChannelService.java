package dev.roelofr.domains.chat;

import dev.roelofr.Constants;
import dev.roelofr.domains.chat.model.ChatEntry;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class ChatChannelService {
    @Inject
    @Broadcast
    @Channel(Constants.Channels.CHAT_ENTRIES)
    protected Emitter<ChatEntry> chatEntrySink;

    @Inject
    @Channel(Constants.Channels.CHAT_ENTRIES)
    protected Multi<ChatEntry> chatEntrySource;

    public Multi<ChatEntry> getChatEntries() {
        return chatEntrySource;
    }

    public void sendChatEntry(ChatEntry entry) {
        chatEntrySink.send(entry);
    }
}
