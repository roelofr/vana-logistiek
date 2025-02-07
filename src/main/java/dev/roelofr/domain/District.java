package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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
