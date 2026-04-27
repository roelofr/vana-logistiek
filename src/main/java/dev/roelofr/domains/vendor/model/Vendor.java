package dev.roelofr.domains.vendor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.roelofr.AppUtil;
import dev.roelofr.domain.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Table(name = "vendors")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NamedQueries({
    @NamedQuery(
        name = "Vendor.getAllSorted",
        query = """
            SELECT v
            FROM Vendor v
            JOIN FETCH v.district
            ORDER BY v.numberNumeric, v.number
            """),
    @NamedQuery(
        name = "Vendor.getAllSortedForUser",
        query = """
            SELECT DISTINCT v
            FROM Vendor v
            LEFT JOIN FETCH v.district d
            LEFT JOIN d.group g
            LEFT JOIN g.users u
            ORDER BY
                CASE WHEN u.id = :userId THEN 0 ELSE 1 END ASC,
                v.numberNumeric ASC,
                v.number ASC
            """),
    @NamedQuery(
        name = "Vendor.getSortedInDistrict",
        query = """
            SELECT v
            FROM Vendor v
            WHERE v.district = ?1
            ORDER BY v.numberNumeric, v.number
            """),
    @NamedQuery(
        name = "Vendor.getSortedInGroup",
        query = """
            SELECT v
            FROM Vendor v
            WHERE v.district NOT IN (
                SELECT d
                FROM District d
                WHERE d.group = ?1
            )
            ORDER BY v.numberNumeric, v.number
            """),
})
public class Vendor extends Model {
    @Column(length = 10)
    String number;

    @Setter(AccessLevel.NONE)
    @Column(name = "number_numeric", columnDefinition = "smallint")
    Integer numberNumeric;

    @Column(length = 200)
    String name;

    @JsonProperty("type")
    @Column(name = "vendor_type", length = 50)
    String vendorType;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    @JsonIgnoreProperties({"vendors"})
    District district;

    @PrePersist
    public void determineNumberNumeric() {
        numberNumeric = AppUtil.parseVendorNumberToInteger(number);
    }
}
