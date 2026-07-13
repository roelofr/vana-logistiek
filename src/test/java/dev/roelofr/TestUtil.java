package dev.roelofr;

import dev.roelofr.domains.chat.model.Chat;
import dev.roelofr.domains.chat.model.ChatRepository;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.GroupRepository;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserRepository;
import dev.roelofr.domains.vendor.model.District;
import dev.roelofr.domains.vendor.model.DistrictRepository;
import dev.roelofr.domains.vendor.model.Vendor;
import dev.roelofr.domains.vendor.model.VendorRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class TestUtil {
    private static final AtomicInteger ChatIncrementer = new AtomicInteger(1);
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ChatRepository chatRepository;
    private final VendorRepository vendorRepository;
    private final DistrictRepository districtRepository;

    protected User createOrFindUser(String providerId) {
        return userRepository.findByProviderId(providerId)
            .orElseGet(() -> {
                var newEntity = User.builder()
                    .name(providerId)
                    .email("%s@example.com".formatted(providerId))
                    .providerId(providerId)
                    .build();

                QuarkusTransaction.requiringNew().run(() -> userRepository.persist(newEntity));

                return newEntity;
            });
    }

    protected Group createOrFindGroup(String name) {
        return groupRepository.findByName(name)
            .orElseGet(() -> {
                var newEntity = Group.builder()
                    .name(name)
                    .build();

                QuarkusTransaction.requiringNew().run(() -> groupRepository.persist(newEntity));

                return newEntity;
            });
    }

    protected Vendor createOrFindVendor(String number, String name) {
        return vendorRepository.findByNumber(number)
            .orElseGet(() -> {
                var newEntity = Vendor.builder()
                    .number(number)
                    .name(name)
                    .build();

                QuarkusTransaction.requiringNew().run(() -> vendorRepository.persist(newEntity));

                return newEntity;
            });
    }

    protected District createOrFindDistrict(String name) {
        return districtRepository.findByName(name)
            .orElseGet(() -> {
                var newEntity = District.builder()
                    .name(name)
                    .build();

                QuarkusTransaction.requiringNew().run(() -> districtRepository.persist(newEntity));

                return newEntity;
            });
    }

    public User createUser(String providerId, List<String> groups) {
        var user = createOrFindUser(providerId);

        if (groups != null)
            user.setGroups(
                groups.stream().map(this::createOrFindGroup).collect(Collectors.toSet())
            );

        return user;
    }

    public Group createGroup(String name, List<String> users) {
        var group = createOrFindGroup(name);

        if (users != null)
            group.setUsers(
                users.stream().map(this::createOrFindUser).toList()
            );

        return group;
    }

    public Chat createChat(List<String> users, List<String> groups) {
        return createChat(String.format("Test Chat %06d", ChatIncrementer.getAndIncrement()), users, groups);
    }

    public Chat createChat(String name, List<String> users, List<String> groups) {
        var newChat = Chat.builder()
            .title(name)
            .build();

        QuarkusTransaction.requiringNew().run(() -> {
            chatRepository.persist(newChat);

            if (users != null)
                users.stream().map(this::createOrFindUser).forEach(newChat::addUser);
            if (groups != null)
                groups.stream().map(this::createOrFindGroup).forEach(newChat::addGroup);
        });

        return newChat;
    }

    public Vendor createVendor(String number, String name, String district) {
        var vendor = createOrFindVendor(number, name);
        vendor.setName(name);

        if (district != null)
            vendor.setDistrict(createOrFindDistrict(district));

        return vendor;
    }

    public Vendor createVendor(String number, String name) {
        return createVendor(number, name, null);
    }
}
