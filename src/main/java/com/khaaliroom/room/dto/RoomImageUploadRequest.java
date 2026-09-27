package com.khaaliroom.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to generate a presigned S3 upload URL")
public record RoomImageUploadRequest(

        @NotBlank(message = "Content type is required")
        @Size(max = 100, message = "Content type must not exceed 100 characters")
        @Schema(
                description = "MIME type of the image",
                example = "image/jpeg"
        )
        String contentType

) {
}