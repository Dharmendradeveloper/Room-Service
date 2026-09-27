package com.khaaliroom.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Presigned S3 upload information")
public record RoomImageUploadResponse(

        @Schema(
                description = "Unique identifier assigned to the image",
                example = "7d8f5a3e-8b4d-4f1a-9e23-2a5d8c7f1234"
        )
        UUID imageId,

        @Schema(
                description = "S3 object key where the image will be stored",
                example = "rooms/580fe919-5182-4288-a60f-ea998b35ae54/7d8f5a3e-8b4d-4f1a-9e23-2a5d8c7f1234.jpg"
        )
        String objectKey,

        @Schema(
                description = "Temporary URL used by the client to upload the image directly to S3"
        )
        String uploadUrl,

        @Schema(
                description = "Number of minutes until the upload URL expires",
                example = "10"
        )
        int expiresInMinutes

) {
}