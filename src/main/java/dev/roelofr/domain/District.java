package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Entity
@Cacheable
@Table(name = "districts")
@EqualsAndHashCode(callSuper = true)
public class District extends Model {
    @Column(length = 50, nullable = false)
    String name;

    @Column(name = "mobile_name", length = 3)
    String mobileName;

    @Column(length = 50)
    String colour;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    List<User> users;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    List<Vendor> vendors;
}
