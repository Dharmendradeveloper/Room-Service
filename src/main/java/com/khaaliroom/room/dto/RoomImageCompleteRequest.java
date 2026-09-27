package com.khaaliroom.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to complete a room image upload")
public record RoomImageCompleteRequest(

        @NotNull(message = "Image ID is required")
        @Schema(
                description = "Image ID returned when the presigned upload URL was generated",
                example = "f3318659-171c-4fe7-a174-6ca2f3d76fc0"
        )
        UUID imageId,

        @NotNull(message = "Display order is required")
        @Min(
                value = 1,
                message = "Display order must be at least 1"
        )
        @Max(
                value = 4,
                message = "Display order must not exceed 4"
        )
        @Schema(
                description = "Display order of the image. Values must be between 1 and 4.",
                example = "1"
        )
        Integer displayOrder

) {
}

