package dev.roelofr.domains.chat.dto;

import dev.roelofr.domain.dto.Location;
import dev.roelofr.domains.chat.model.ChatSubject;
import dev.roelofr.domains.issue.Issue;
import dev.roelofr.domains.vendor.model.Vendor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ChatSubjectDto(
    long id,
    ChatSubjectVendorDto vendor,
    Location location
) {
    public static ChatSubjectDto fromNullable(ChatSubject subject) {
        if (subject == null)
            return null;

        if (subject instanceof Issue issue) {
            if (issue.getVendor() != null)
                return new ChatSubjectDto(issue.getId(), new ChatSubjectVendorDto(issue.getVendor()), null);
            if (issue.getLocation() != null)
                return new ChatSubjectDto(issue.getId(), null, issue.getLocation());
            return new ChatSubjectDto(issue.getId(), null, null);
        }

        log.warn("Somehow, got a {} as ChatSubject", subject.getClass().getName());

        return null;
    }

    public record ChatSubjectVendorDto(long id, String number, String name) {
        public ChatSubjectVendorDto(Vendor vendor) {
            this(vendor.getId(), vendor.getNumber(), vendor.getName());
        }
    }
}
