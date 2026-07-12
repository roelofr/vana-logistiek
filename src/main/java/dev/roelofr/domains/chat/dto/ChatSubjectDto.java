package dev.roelofr.domains.chat.dto;

import dev.roelofr.domain.dto.Location;
import dev.roelofr.domains.chat.model.ChatSubject;
import dev.roelofr.domains.issue.Issue;
import dev.roelofr.domains.vendor.model.Vendor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Builder
public record ChatSubjectDto(
    long id,
    ChatSubjectVendorDto vendor,
    Location location,
    LocalDateTime createdAt,
    LocalDateTime resolvedAt
) {
    public static ChatSubjectDto fromNullable(ChatSubject subject) {
        if (subject == null)
            return null;

        if (subject instanceof Issue issue) {
            var dtoBuilder = builder()
                .id(issue.getId())
                .createdAt(issue.getCreatedAt())
                .resolvedAt(issue.getResolvedAt());

            if (issue.getVendor() != null)
                return dtoBuilder
                    .vendor(new ChatSubjectVendorDto(issue.getVendor()))
                    .build();

            if (issue.getLocation() != null)
                return dtoBuilder
                    .location(issue.getLocation())
                    .build();

            return dtoBuilder.build();
        }

        log.warn("Somehow, got a {} as ChatSubject", subject.getClass().getName());

        return null;
    }

    public record ChatSubjectVendorDto(long id, String number, String name, String icon, String colour) {
        public ChatSubjectVendorDto(Vendor vendor) {
            this(vendor.getId(), vendor.getNumber(), vendor.getName(), vendor.getIcon(), vendor.getColour());
        }
    }
}
