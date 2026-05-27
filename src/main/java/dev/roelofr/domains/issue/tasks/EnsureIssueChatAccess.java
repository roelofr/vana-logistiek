package dev.roelofr.domains.issue.tasks;

import dev.roelofr.domains.chat.ChatService;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.issue.model.IssueParticipant;
import dev.roelofr.events.ModelCreatedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class EnsureIssueChatAccess {
    private final ChatRepository chatRepository;
    private final ChatService chatService;

    @Transactional
    public void ensureIssueAccessGivesChatAccess(@ObservesAsync ModelCreatedEvent<IssueParticipant> event) {
        var participant = event.getModel();
        var issue = participant.getIssue();

        if (issue == null)
            return;

        var chat = chatRepository.findByKey(issue.getChatKey());
        if (chat.isEmpty())
            return;

        if (participant.getUser() != null)
            chatService.addChatParticipant(chat.get(), participant.getUser());
        else
            chatService.addChatParticipant(chat.get(), participant.getGroup());
    }
}
