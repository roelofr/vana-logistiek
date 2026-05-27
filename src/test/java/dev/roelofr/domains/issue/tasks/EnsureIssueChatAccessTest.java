package dev.roelofr.domains.issue.tasks;

import dev.roelofr.domains.chat.ChatService;
import dev.roelofr.domains.issue.model.IssueService;
import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.UserTestService;
import dev.roelofr.domains.vendor.service.VendorService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class EnsureIssueChatAccessTest {
    @Inject
    IssueService issueService;
    @Inject
    VendorService vendorService;
    @Inject
    UserService userService;
    @Inject
    ChatService chatService;
    @Inject
    UserTestService userTestService;

    @Test
    void ensureIssueAccessGivesChatAccess() {
        var vendor = vendorService.getVendor("100a").orElseThrow();
        var user = userTestService.findTestUser("user");
        var user2 = userTestService.findTestUser("new-user");

        var issue = issueService.createIssue(vendor, user, "Test Subject");

        assertNotNull(issue);
        assertNotNull(issue.getChatKey());

        assertTrue(issueService.isParticipant(issue, user));
        assertFalse(issueService.isParticipant(issue, user2));

        var chat = chatService.findByKey(issue.getChatKey()).orElseThrow();
        assertNotNull(chat);
        assertTrue(chatService.isVisibleForUser(chat, user));
        assertFalse(chatService.isVisibleForUser(chat, user2));

        // Add the user
        issueService.addParticipant(issue, user2);

        // Event should trigger here.

        assertTrue(issueService.isParticipant(issue, user2));
        assertTrue(chatService.isVisibleForUser(chat, user2));
    }
}
