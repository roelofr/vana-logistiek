package dev.roelofr.domains.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.Views;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
