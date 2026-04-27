package dev.roelofr.domains.issue.model;

import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "issue_participants")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class IssueParticipant extends Model {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issue_id")
    Issue issue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user;

    @CreationTimestamp
    LocalDateTime createdAt;

    @PrePersist
    void ensureRelation() {
        if (issue == null)
            throw new IssueRelationException(IssueRelationException.Error.NoIssue);

        if (user == null && group == null)
            throw new IssueRelationException(IssueRelationException.Error.NoParticipant);

        if (user != null && group != null)
            throw new IssueRelationException(IssueRelationException.Error.AmbiguousParticipant);
    }

    public boolean matchesUser(User target) {
        if (user != null)
            return user.is(target);

        if (group != null)
            return group.hasUser(target);

        throw new RuntimeException("Found participant without group and user, somehow");
    }

    @Getter
    static class IssueRelationException extends RuntimeException {
        final Error error;

        private IssueRelationException(Error error) {
            super(error.message);
            this.error = error;
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        public enum Error {
            NoIssue("IssueParticipant must have an issue"),
            NoParticipant("IssueParticipant must have a participant"),
            AmbiguousParticipant("IssueParticipant must have either a group, or a user as participant, not both");

            final String message;
        }
    }

    static IssueParticipant forUser(User user) {
        return IssueParticipant.builder().user(user).build();
    }

    static IssueParticipant forGroup(Group group) {
        return IssueParticipant.builder().group(group).build();
    }
}
