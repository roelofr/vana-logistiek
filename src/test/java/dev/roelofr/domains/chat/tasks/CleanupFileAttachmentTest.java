package dev.roelofr.domains.chat.tasks;

import dev.roelofr.domains.chat.model.ChatEntryRepository;
import dev.roelofr.domains.chat.model.ChatFile;
import dev.roelofr.domains.chat.model.ChatMessage;
import dev.roelofr.domains.chat.model.FileStatus;
import dev.roelofr.events.ChatFileUploaded;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@QuarkusTest
class CleanupFileAttachmentTest {
    @InjectMock
    ChatEntryRepository chatEntryRepository;

    @Inject
    CleanupFileAttachment cleanupFileAttachment;

    final String TEST_IMAGE_PATH = "src/test/resources/test-images";
    File testFile;

    @BeforeEach
    void createTestFile(@TempDir Path tempDir) throws IOException {
        var target = tempDir.resolve("./image.jpeg");

        Files.copy(Path.of(TEST_IMAGE_PATH, "amanda-swanepoel.jpeg"), target);

        testFile = target.toFile();
    }

    @Test
    void convertUnprocessed() {
        var fakeException = new RuntimeException("Test ok");
        var mockedEntry = mock(ChatFile.class);
        given(mockedEntry.getPath()).willThrow(fakeException);

        var thrown = assertThrowsExactly(fakeException.getClass(), () -> cleanupFileAttachment.convertUnprocessed());

        assertSame(fakeException, thrown);
    }

    @Test
    void convertOnCreationNonMedia() {
        var mockedMessage = mock(ChatMessage.class);
        var dummyMessage = ChatMessage.builder()
            .id(1L)
            .build();

        given(chatEntryRepository.findById(1L)).willReturn(mockedMessage);

        assertDoesNotThrow(() -> cleanupFileAttachment.convertOnCreation(
            new ChatFileUploaded(dummyMessage)
        ));

        then(mockedMessage).shouldHaveNoInteractions();
    }

    @Test
    void convertOnCreationOfMedia() {
        var testPath = testFile.toPath();
        ChatFile file = ChatFile.builder()
            .id(666L)
            .path(testPath.toString())
            .fileStatus(FileStatus.New)
            .filename("test.dat")
            .build();

        given(chatEntryRepository.findById(file.getId())).willReturn(file);

        assertDoesNotThrow(() -> cleanupFileAttachment.convertOnCreation(
            new ChatFileUploaded(file)
        ));

        assertNotSame(testPath, file.getPath());
        assertEquals("test.jpeg", file.getFilename());
        assertEquals(FileStatus.Ready, file.getFileStatus());

        assertTrue(Files.exists(Path.of(file.getPath())), "Failed asserting new file exists");
        assertFalse(Files.exists(testPath), "Failed asserting file is deleted");
    }

    @Test
    void convertChatFile() {
    }
}
