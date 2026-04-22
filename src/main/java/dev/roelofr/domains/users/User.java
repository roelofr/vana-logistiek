package dev.roelofr.domains.users;

import com.fasterxml.jackson.annotation.JsonView;
import dev.roelofr.domain.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
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
    @JsonView({Views.Private.class, Views.Admin.class})
    List<String> roles = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    @JsonView({Views.Public.class})
    List<Group> groups;
}
