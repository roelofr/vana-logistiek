package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "districts")
public class District extends Model {
    @Column(length = 50, nullable = false)
    public String name;

    @Column(name = "mobile_name", length = 3)
    public String mobileName;

    @Column(length = 50)
    public String colour;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    public List<User> users;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    public List<Vendor> vendors;
}
