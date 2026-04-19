package dev.roelofr.domains.users;

import dev.roelofr.domain.Model;
import dev.roelofr.domain.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groups")
@EqualsAndHashCode(callSuper = true)
public class Group extends Model {
    @Column(name = "provider_id", length = 50)
    String providerId;

    @Column(length = 100)
    String name;

    @Column(length = 30)
    String label;

    @Builder.Default
    @Column(columnDefinition = "json")
    List<String> roles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @ManyToMany
    @JoinTable(
        name = "group_users",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<User> users;
}
