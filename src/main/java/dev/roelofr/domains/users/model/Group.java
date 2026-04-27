package dev.roelofr.domains.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.domain.Model;
import dev.roelofr.domain.Team;
import dev.roelofr.domains.users.Views;
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
    @JsonView({Views.Admin.class})
    String providerId;

    @Column(length = 100)
    @JsonView({Views.Public.class})
    String name;

    @JsonIgnore
    @Column(length = 30)
    String label;

    @Builder.Default
    @Column(columnDefinition = "json")
    @JsonView({Views.Admin.class})
    List<String> roles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonView({Views.Public.class})
    Team team;

    @ManyToMany
    @JoinTable(
        name = "group_users",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonView({Views.Public.class})
    @JsonIgnoreProperties({"groups"})
    List<User> users;

    public boolean hasUser(User user) {
        return users.stream().anyMatch(groupUser -> groupUser.is(user));
    }
}
