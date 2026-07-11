package dev.roelofr.domains.issue;

import dev.roelofr.domain.dto.Location;
import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.vendor.model.Vendor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;

    @Transactional
    public Issue create(Chat createdChat, Vendor vendor, Location location) {
        var issue = Issue.builder()
            .chat(createdChat)
            .vendor(vendor)
            .location(location)
            .build();

        issueRepository.persist(issue);

        return issue;
    }

    public List<Issue> findByVendor(Vendor vendor) {
        return issueRepository.findByVendor(vendor);
    }
}
