package dev.roelofr.domains.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A message a user has sent.
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(value = ChatFile.TYPE)
public class ChatFile extends AttributedChatEntry {
    public static final String TYPE = "file";

    @JsonIgnore
    @Column(name = "file_path", length = 200)
    String path;

    @Column(name = "file_name", length = 200)
    String filename;

    @Column(name = "file_mime", length = 100)
    String mimetype;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "file_status", length = 10)
    FileStatus fileStatus = FileStatus.New;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", length = 10)
    FileType fileType = FileType.Unknown;

    public boolean isFileReady() {
        return path != null && fileStatus.equals(FileStatus.Ready);
    }

    public boolean isFileCorrupted() {
        return fileStatus.equals(FileStatus.Corrupted);
    }

    @Override
    @JsonInclude
    public String getType() {
        return TYPE;
    }

    @JsonInclude
    public String getUrl() {
        return String.format("/api/files/chat/%d/image/%d/%s", getChat().getId(), getId(), filename);
    }
}
