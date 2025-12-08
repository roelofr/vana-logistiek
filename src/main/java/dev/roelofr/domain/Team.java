package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.arc.All;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class Team extends Model {
    @Column(length = 50, nullable = false)
    String name;

    @Column(length = 50)
    String colour;

    @Column(length = 50)
    String icon;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    List<User> users;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    List<District> districts;
}
