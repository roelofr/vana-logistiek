package dev.roelofr.domains.vendor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.roelofr.domain.Model;
import dev.roelofr.domain.Team;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Entity
@Cacheable
@SuperBuilder
@Table(name = "districts")
@EqualsAndHashCode(callSuper = true)
public class District extends Model {
    @Column(length = 50, nullable = false)
    String name;

    @Column(length = 50)
    String colour;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @OneToMany(mappedBy = "district")
    @JsonIgnoreProperties({"district"})
    @ToString.Exclude
    List<Vendor> vendors;
}
