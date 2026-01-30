package dev.roelofr.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.roelofr.AppUtil;
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
        query = "from Vendor v join fetch v.district order by v.numberNumeric, v.number"),
    @NamedQuery(
        name = "Vendor.getSortedInDistrict",
        query = "from Vendor v where v.district = ?1 order by v.numberNumeric, v.number"),
    @NamedQuery(
        name = "Vendor.getSortedInTeam",
        query = "from Vendor v where v.district in (select d from District d where d.team = ?1) order by v.numberNumeric, v.number"),
    @NamedQuery(
        name = "Vendor.getSortedNotInTeam",
        query = "from Vendor v where v.district not in (select d from District d where d.team = ?1) order by v.numberNumeric, v.number")
})
public class Vendor extends Model {
    @Column(length = 10)
    String number;

    @Setter(AccessLevel.NONE)
    @Column(name = "number_numeric", columnDefinition = "smallint")
    Integer numberNumeric;

    @Column(length = 200)
    String name;

    @Column(name = "vendor_type", length = 50)
    String vendorType;

    @ManyToOne
    @JoinColumn(name = "district_id")
    @JsonIgnoreProperties({"vendors"})
    District district;

    @PrePersist
    public void determineNumberNumeric() {
        numberNumeric = AppUtil.parseVendorNumberToInteger(number);
    }
}
