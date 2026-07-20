package dev.roelofr.domains.vendor.dto;

import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserFlags;
import dev.roelofr.domains.vendor.model.District;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.model.VendorType;

import java.util.List;

public record DistrictDto(
    long id,
    String name,
    String colour,
    DistrictGroup group,
    List<DistrictVendor> vendors,
    List<DistrictUser> users
) {
    public DistrictDto(District district, Group group, List<Vendor> vendors, List<User> users) {
        this(
            district.getId(),
            district.getName(),
            district.getColour(),
            new DistrictGroup(group),
            vendors.stream().map(DistrictVendor::new).toList(),
            users.stream().map(DistrictUser::new).toList()
        );
    }

    public record DistrictGroup(
        long id,
        String name,
        String colour,
        String icon
    ) {
        public DistrictGroup(Group group) {
            this(group.getId(), group.getName(), group.getColour(), group.getIcon());
        }
    }

    public record DistrictVendor(
        long id,
        String number,
        String name,
        VendorType type
    ) {
        public DistrictVendor(Vendor vendor) {
            this(vendor.getId(), vendor.getNumber(), vendor.getName(), vendor.getVendorType());
        }
    }

    public record DistrictUser(
        long id,
        String name,
        String avatar,
        Boolean atWork
    ) {
        public DistrictUser(User user) {
            this(user.getId(), user.getName(), user.getAvatar(), user.hasFlag(UserFlags.Active));
        }
    }
}
