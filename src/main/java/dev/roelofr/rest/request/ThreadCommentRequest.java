package dev.roelofr.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

public record ThreadCommentRequest(
    @RestForm @NotBlank @Length(max = 1024 * 1000) String message,
    @RestForm("file") List<@NotNull FileUpload> files
) {
}
