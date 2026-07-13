package dev.roelofr.domains.issue;

import dev.roelofr.domains.chat.ChatService;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.vendor.model.Vendor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class IssueRepository implements PanacheRepository<Issue> {
    private final ChatService chatService;

    @Inject
    public IssueRepository(ChatService chatService) {
        this.chatService = chatService;
    }

    public Optional<Issue> findByChat(Chat chat) {
        return find("chat", chat).singleResultOptional();
    }

    public List<Issue> findByVendor(Vendor vendor) {
        var issues = list("#Issue.listByVendorWithChat", Map.of("vendor", vendor));

        chatService.fetchParticipantsForChatIds(issues.stream().map(issue -> issue.getChat().getId()).toList());

        return issues;
    }
}
