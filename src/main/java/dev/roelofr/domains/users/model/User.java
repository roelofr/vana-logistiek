package dev.roelofr.domains.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.Views;
import dev.roelofr.domains.users.jpa.UserFlagsConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
@NamedQueries({
    @NamedQuery(name = "User.findByProviderIdWithRelations", query = """
            SELECT u FROM User u
            LEFT JOIN FETCH u.groups g
            WHERE u.providerId = :subject
        """),
    @NamedQuery(name = "User.listAllWithGroup", query = """
            SELECT u
            FROM User u
            LEFT JOIN FETCH u.groups g
            ORDER BY u.name, u.id
        """)
})
public class User extends Model {
    @Column(name = "provider_id", length = 50)
    @JsonView({Views.Private.class, Views.Admin.class})
    String providerId;

    @Column(length = 100)
    @JsonView(Views.Public.class)
    String name;

    @Column(length = 100)
    @JsonView({Views.Private.class, Views.Admin.class})
    String email;

    @Column(name = "avatar_url", length = 200)
    String avatar;

    @Builder.Default
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonView({Views.Private.class, Views.Admin.class})
    Set<String> roles = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "users")
    @JsonView({Views.Public.class})
    @JsonIgnoreProperties({"users"})
    List<Group> groups = new ArrayList<>();

    @JsonView({Views.Private.class})
    @Convert(converter = UserFlagsConverter.class)
    @Column(name = "user_flags", nullable = false)
    List<UserFlags> flags = new ArrayList<>();

    public void addGroup(Group wantedGroup) {
        if (groups.stream().anyMatch(existingGroup -> existingGroup.is(wantedGroup)))
            return;

        var newGroups = new ArrayList<>(groups);
        newGroups.add(wantedGroup);
        setGroups(newGroups);
    }

    public void removeGroup(Group wantedGroup) {
        if (groups.stream().noneMatch(existingGroup -> existingGroup.is(wantedGroup)))
            return;

        var newGroups = new ArrayList<>(groups);
        newGroups.removeIf(group -> group.is(wantedGroup));
        setGroups(newGroups);
    }

    public boolean hasRole(String role) {
        return roles.contains(role.toLowerCase());
    }

    public void addRole(String role) {
        var roleCopy = new HashSet<>(roles);
        roleCopy.add(role.toLowerCase());
        roles = roleCopy;
    }

    public void removeRole(String role) {
        var roleCopy = new HashSet<>(roles);
        roleCopy.remove(role.toLowerCase());
        roles = roleCopy;
    }

    public boolean hasFlag(UserFlags userFlags) {
        return flags.contains(userFlags);
    }

    public void addFlag(UserFlags userFlags) {
        if (hasFlag(userFlags))
            return;

        var newFlags = new ArrayList<>(flags);
        newFlags.add(userFlags);
        flags = newFlags;
    }
}
