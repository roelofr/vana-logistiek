package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

import java.util.List;

@Data
@Entity
@Builder
@Cacheable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teams")
@EqualsAndHashCode(callSuper = true)
@NamedQueries({
    @NamedQuery(
        name = "Team.getLikeName",
        query = """
            SELECT Team
            FROM Team
            WHERE
                LOWER(name) = ?1
                OR LOWER(name) LIKE CONCAT('%', ?1)
                OR LOWER(name) LIKE CONCAT(?1, '%')
            """)
})
public class Team extends Model {
    @Column(length = 50, nullable = false)
    String name;

    @Builder.Default
    @Column(name = "required", updatable = false, nullable = false)
    boolean system = false;

    @Column(length = 50)
    String colour;

    @Column(length = 50)
    String icon;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    List<User> users;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    List<District> districts;

    @JsonGetter("icon")
    public String getJsonIcon() {
        if (icon == null)
            return null;

        if (icon.startsWith("i-"))
            return icon;

        return String.format("i-lucide-%s", icon);
    }
}
