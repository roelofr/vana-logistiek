package dev.roelofr.domains.issue;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.vendor.model.Vendor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;

    @Transactional
    public Issue create(Chat createdChat, Vendor vendor) {
        var issue = Issue.builder()
            .chat(createdChat)
            .vendor(vendor)
            .build();

        issueRepository.persist(issue);

        return issue;
    }
}
