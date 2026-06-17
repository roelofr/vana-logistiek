package dev.roelofr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.roelofr.domains.users.model.Group;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class TestProvisioning {
    private final TestUtil testUtil;

    @Startup
    void provision() {
        var configFile = readFile();

        configFile.users().forEach(this::provisionUser);
        configFile.chats().forEach(this::provisionChat);
    }

    @Transactional
    void provisionUser(String name, ProvisionUser data) {
        var user = testUtil.createUser(name, data.groups);
        user.setRoles(data.roles);

        log.info("Created user {} in groups [{}} with roles [{}]", user.getName(), user.getGroups().stream().map(Group::getName).toList(), user.getRoles());
    }

    @Transactional
    void provisionChat(ProvisionChat chat) {
        var newChat = testUtil.createChat(chat.name, chat.users, chat.groups);

        log.info("Created chat {} with users [{}] and groups [{}]",
            newChat.getTitle(),
            newChat.getUsers().stream().map(u -> u.getUser().getProviderId()).toList(),
            newChat.getGroups().stream().map(g -> g.getGroup().getName()).toList()
        );
    }

    ProvisionFile readFile() {
        // Read Yaml
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("provisioning.yaml")) {

            if (input == null)
                throw new RuntimeException("Provisioning file provisioning.yaml not found in classpath");

            var mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(input, ProvisionFile.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load YAML configuration", e);
        }
    }

    record ProvisionFile(
        Map<String, ProvisionUser> users,
        List<ProvisionChat> chats
    ) {

    }

    record ProvisionUser(List<String> groups, List<String> roles) {

    }

    record ProvisionChat(String name, List<String> groups, List<String> users) {

    }
}
