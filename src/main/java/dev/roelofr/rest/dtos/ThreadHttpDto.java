package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Thread;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ThreadHttpDto(long id,
                            EmbeddedVendor vendor,
                            EmbeddedUser user,
                            EmbeddedTeam team,
                            EmbeddedUser assignedUser,
                            EmbeddedTeam assignedTeam,
                            String subject) {
    public ThreadHttpDto(@Nonnull Thread thread) {
        this(
            thread.getId(),
            EmbeddedVendor.fromNullable(thread.getVendor()),
            EmbeddedUser.fromNullable(thread.getUser()),
            EmbeddedTeam.fromNullable(thread.getTeam()),
            EmbeddedUser.fromNullable(thread.getAssignedUser()),
            EmbeddedTeam.fromNullable(thread.getAssignedTeam()),
            thread.getSubject()
        );
    }

    public static ThreadHttpDto fromNullable(@Nullable Thread thread) {
        return (thread == null) ? null : new ThreadHttpDto(thread);
    }
}
