package dev.roelofr.domains.vendor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.roelofr.domain.Model;
import dev.roelofr.domains.users.model.Group;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Entity
@Cacheable
@SuperBuilder
@NoArgsConstructor
@Table(name = "districts")
@EqualsAndHashCode(callSuper = true)
@NamedQueries({
    @NamedQuery(
        name = "District.getAllWithRelations",
        query = """
            SELECT d
            FROM District d
            LEFT JOIN FETCH d.group
            LEFT JOIN FETCH d.vendors
            ORDER BY d.name ASC, d.id ASC
            """
    )
})
public class District extends Model {
    @Column(length = 50, nullable = false)
    String name;

    @Column(length = 50)
    String colour;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnoreProperties({"roles", "users", "districts"})
    Group group;

    @OneToMany(mappedBy = "district")
    @JsonIgnoreProperties({"district"})
    @ToString.Exclude
    List<Vendor> vendors;
}
