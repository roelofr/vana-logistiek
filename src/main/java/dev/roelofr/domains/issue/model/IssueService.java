package dev.roelofr.domains.issue.model;

import dev.roelofr.Constants;
import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.GroupService;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.vendor.model.Vendor;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@ApplicationScoped
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final GroupService groupService;

    public List<Issue> getAllSorted() {
        return issueRepository.list("#Issue.findForUserSorted");
    }

    public List<Issue> getSortedForUser(User user) {
        var params = Parameters.with("user", user).and("groups", user.getGroups());

        return issueRepository.list("#Issue.ListSortedForUser", params);
    }

    public Optional<Issue> getIssue(long issueId) {
        return issueRepository.find("#Issue.findByIdWithAllRelations", issueId).singleResultOptional();
    }

    @Transactional
    public Issue createIssue(Vendor vendor, User user, String subject) {
        var cpGroup = groupService.findByLabelOrFail(Constants.Groups.CP);
        var vendorGroup = vendor.getDistrict().getGroup();

        var issue = Issue.builder()
            .subject(subject)
            .vendor(vendor)
            .user(user)
            .group(vendorGroup)
            .assignedGroup(cpGroup)
            .build();

        // See if there's a group to give access
        if (issue.getGroup() != null)
            addParticipant(issue, issue.getGroup());

        // Otherwise, allow the user to access
        addParticipantUnlessAccess(issue, user);

        // Allow the assigned group to access
        addParticipant(issue, cpGroup);

        issueRepository.persist(issue);

        return issue;
    }

    public boolean isParticipant(Issue issue, User user) {
        return issue.getParticipants()
            .stream()
            .anyMatch(participant -> participant.matchesUser(user));
    }

    public void addParticipantUnlessAccess(Issue issue, User user) {
        if (user == null) return;

        if (isParticipant(issue, user)) return;

        addParticipant(issue, user);
    }

    /**
     * Adds a User as a participant.
     * Creates a new IssueParticipant if one doesn't already exist for this user.
     */
    public void addParticipant(Issue issue, User user) {
        if (user == null) return;

        addUniqueParticipant(issue, IssueParticipant.forUser(user), IssueParticipant::getUser);
    }

    /**
     * Adds a Group as a participant.
     * Creates a new IssueParticipant if one doesn't already exist for this group.
     */
    public void addParticipant(Issue issue, Group group) {
        if (group == null) return;

        addUniqueParticipant(issue, IssueParticipant.forGroup(group), IssueParticipant::getGroup);
    }

    /**
     * Removes a User from the participants.
     * Finds the participant linked to this user and removes it.
     */
    public void removeParticipant(Issue issue, User user) {
        if (user == null) return;

        removeUniqueParticipant(issue, user, IssueParticipant::getUser);
    }

    /**
     * Removes a Group from the participants.
     * Finds the participant linked to this group and removes it.
     */
    public void removeParticipant(Issue issue, Group group) {
        if (group == null) return;

        removeUniqueParticipant(issue, group, IssueParticipant::getGroup);
    }

    /**
     * Internal helper to add a participant, ensuring no duplicates are introduced.
     *
     * @param issue       Issue to work against
     * @param participant The target participant to add to the thread.
     * @param extractor   A function to extract the target entity (User or Group) from a participant.
     */
    @Transactional
    void addUniqueParticipant(Issue issue, IssueParticipant participant, Function<IssueParticipant, ? extends Model> extractor) {
        var target = extractor.apply(participant);
        if (target == null)
            throw new NullPointerException("IssueParticipant must be set");

        var participants = issue.getParticipants();
        var exists = participants.stream().anyMatch(p -> target.is(extractor.apply(p)));

        if (exists) return;

        participants.add(participant);
        participant.setIssue(issue);
    }

    /**
     * Internal helper to find and remove a participant based on a target entity (User or Group).
     *
     * @param issue     Issue to work against
     * @param target    The target entity to match against.
     * @param extractor A function to extract the target entity (User or Group) from a participant.
     */
    @Transactional
    void removeUniqueParticipant(Issue issue, Model target, Function<IssueParticipant, ? extends Model> extractor) {
        if (target == null)
            throw new NullPointerException("IssueParticipant must be set");

        var participants = issue.getParticipants();

        // Find the participant in the list that matches the target
        IssueParticipant toRemove = participants.stream()
            .filter(p -> target.is(extractor.apply(p)))
            .findFirst()
            .orElse(null);

        // Remove if found
        if (toRemove != null) {
            participants.remove(toRemove);
            toRemove.setIssue(null);
        }
    }
}
