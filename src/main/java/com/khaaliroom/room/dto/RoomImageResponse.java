package com.khaaliroom.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Room image information")
public record RoomImageResponse(

        @Schema(
                description = "Unique identifier of the image",
                example = "7d8f5a3e-8b4d-4f1a-9e23-2a5d8c7f1234"
        )
        UUID id,

        @Schema(
                description = "ID of the room this image belongs to",
                example = "580fe919-5182-4288-a60f-ea998b35ae54"
        )
        UUID roomId,

        @Schema(
                description = "Public URL of the room image",
                example = "https://example-bucket.s3.amazonaws.com/rooms/580fe919/image-1.jpg"
        )
        String imageUrl,

        @Schema(
                description = "Display order of the image. Lower values appear first.",
                example = "1"
        )
        Integer displayOrder,

        @Schema(
                description = "Time when the image was created",
                example = "2026-09-25T10:30:00"
        )
        LocalDateTime createdAt
) {
}