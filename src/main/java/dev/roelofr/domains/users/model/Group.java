package dev.roelofr.domains.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.Views;
import dev.roelofr.domains.users.jpa.GroupFlagConverter;
import dev.roelofr.domains.vendor.model.District;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_groups")
@EqualsAndHashCode(callSuper = true)
@NamedQueries({
    @NamedQuery(
        name = "Group.listAllSorted",
        query = """
            SELECT gr
            FROM Group gr
            ORDER BY gr.name ASC, gr.id ASC

            """),
    @NamedQuery(
        name = "Group.listAllWithGroupsSorted",
        query = """
            SELECT gr
            FROM Group gr
            LEFT JOIN FETCH gr.districts
            ORDER BY gr.name ASC, gr.id ASC
            """),
    @NamedQuery(
        name = "Group.getLikeName",
        query = """
            SELECT g
            FROM Group g
            WHERE
                LOWER(g.name) = LOWER(?1)
                OR LOWER(g.name) LIKE CONCAT('%', LOWER(?1))
                OR LOWER(g.name) LIKE CONCAT(LOWER(?1), '%')
            """),
    @NamedQuery(name = "Group.findByUserIdWithDistrict", query = """
        SELECT g
        FROM Group g
        LEFT JOIN FETCH g.districts
        LEFT JOIN g.users u
        WHERE u = :user
        """)
})
public class Group extends Model {
    @Column(name = "provider_id", length = 50)
    @JsonView({Views.Admin.class})
    String providerId;

    @Column(length = 100)
    @JsonView({Views.Public.class})
    String name;

    @JsonIgnore
    @Column(length = 50)
    String label;

    @Column(length = 30)
    String icon;

    @Column(length = 30)
    String colour;

    @Builder.Default
    @Column(name = "is_system", updatable = false, nullable = false)
    boolean system = false;

    @Builder.Default
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonView({Views.Admin.class})
    List<String> roles = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
        name = "user_user_group",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonView({Views.Public.class})
    @JsonIgnoreProperties({"groups"})
    List<User> users = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "group")
    @JsonIgnoreProperties({"group"})
    List<District> districts = new ArrayList<>();

    @JsonView({Views.Private.class})
    @Convert(converter = GroupFlagConverter.class)
    @Column(name = "group_flags", nullable = false)
    List<GroupFlags> flags = new ArrayList<>();

    public boolean hasUser(User user) {
        return users.stream().anyMatch(groupUser -> groupUser.is(user));
    }

    public void addUser(User user) {
        this.users.add(user);
        user.getGroups().add(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getGroups().remove(this);
    }

    public boolean hasFlag(GroupFlags flag) {
        return this.flags.contains(flag);
    }

    public void addFlag(GroupFlags flags) {
        if (hasFlag(flags))
            return;

        var newFlags = new ArrayList<>(this.flags);
        newFlags.add(flags);
        this.flags = newFlags;
    }

    public boolean hasDistricts() {
        return getDistricts() != null && !getDistricts().isEmpty();
    }
}
