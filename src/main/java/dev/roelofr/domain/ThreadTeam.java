package dev.roelofr.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "thread_teams")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ThreadTeam extends Model {
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "thread_id", nullable = false)
    Thread thread;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "team_id", nullable = false)
    Team team;
}
