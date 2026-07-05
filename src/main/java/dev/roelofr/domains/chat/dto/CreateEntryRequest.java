package dev.roelofr.domains.chat.dto;

import dev.roelofr.validation.CanSelfValidate;
import dev.roelofr.validation.ValidatedItself;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

@ValidatedItself
public record CreateEntryRequest(
    @RestForm("message") String message,
    @RestForm("location") @PartType(MediaType.APPLICATION_JSON) ChatLocationDto location,
    @RestForm(FileUpload.ALL) List<FileUpload> files
) implements CanSelfValidate {
    public boolean hasMessage() {
        return message != null && !message.isBlank();
    }

    public boolean hasLocation() {
        return location != null && location.isValid();
    }

    public boolean hasFiles() {
        return files != null && !files.isEmpty();
    }

    @Override
    public boolean isValid() {
        return hasMessage() || hasLocation() || hasFiles();
    }
}
