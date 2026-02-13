package dev.roelofr.repository;

import dev.roelofr.domain.Team;
import dev.roelofr.domain.Thread;
import dev.roelofr.domain.ThreadUpdate;
import dev.roelofr.domain.User;
import dev.roelofr.domain.enums.UpdateType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@ApplicationScoped
public class ThreadUpdateRepository implements PanacheRepository<ThreadUpdate> {
    public List<ThreadUpdate> findByThread(@Nonnull Thread thread) {
        return list("#ThreadUpdate.findByThreadSorted", Parameters.with("thread", thread));
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public ThreadUpdate persistForType(@NotNull UpdateType type, @NotNull Thread thread, @NotNull User creatingUser, @NotNull Team creatingTeam) {
        var update = switch (type) {
            case Message -> new ThreadUpdate.ThreadMessage();
            case Created -> new ThreadUpdate.ThreadCreated();
            case Resolved -> new ThreadUpdate.ThreadResolved();
            case Attachment -> new ThreadUpdate.ThreadAttachment();
            case AssignToTeam -> new ThreadUpdate.ThreadAssignToTeam();
            case ClaimedByUser -> new ThreadUpdate.ThreadClaimedByUser();

            default -> throw new RuntimeException("Someone forgot to program a type");
        };

        update.setThread(thread);
        update.setUser(creatingUser);
        update.setTeam(creatingTeam);

        persist(update);

        return update;
    }
}
