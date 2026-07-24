package dev.roelofr.domains.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.Roles;
import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.Views;
import dev.roelofr.domains.users.jpa.UserFlagConverter;
import io.quarkus.resteasy.reactive.jackson.SecureField;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Collection;
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
    @SecureField(rolesAllowed = Roles.Admin)
    @JsonView(Views.Public.class)
    Set<String> roles = new HashSet<>();

    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(mappedBy = "users")
    @JsonView({Views.Public.class})
    @JsonIgnoreProperties({"users", "districts"})
    Set<Group> groups = new HashSet<>();

    @JsonView({Views.Private.class})
    @Convert(converter = UserFlagConverter.class)
    @Column(name = "user_flags", nullable = false)
    List<UserFlags> flags = new ArrayList<>();

    public void addGroup(Group group) {
        this.groups.add(group);
        group.getUsers().add(this);
    }

    public void removeGroup(Group group) {
        this.groups.remove(group);
        group.getUsers().remove(this);
    }

    public void setGroups(@Nonnull Collection<Group> newGroups) {
        groups.stream()
            .filter(group -> !newGroups.contains(group))
            .forEach(this::removeGroup);

        newGroups.stream()
            .filter(group -> !groups.contains(group))
            .forEach(this::addGroup);
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

    public boolean hasFlag(UserFlags flag) {
        return flags.contains(flag);
    }

    public void addFlag(UserFlags flag) {
        if (hasFlag(flag))
            return;

        var newFlags = new ArrayList<>(flags);
        newFlags.add(flag);
        setFlags(newFlags);
    }

    public void removeFlag(UserFlags flag) {
        if (!hasFlag(flag))
            return;

        var newFlags = new ArrayList<>(flags);
        newFlags.remove(flag);
        setFlags(newFlags);
    }

    public void setFlag(UserFlags flag, boolean active) {
        if (active)
            addFlag(flag);
        else removeFlag(flag);
    }

    @JsonInclude
    @JsonIncludeProperties({"id", "name", "label", "icon", "colour"})
    public Group getGroup() {
        return groups.stream().findFirst().orElse(null);
    }

    public void setGroup(Group group) {
        setGroups(group != null ? List.of(group) : List.of());
    }
}
