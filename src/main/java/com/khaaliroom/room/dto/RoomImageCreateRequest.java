package com.khaaliroom.room.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoomImageCreateRequest(

        @NotBlank(message = "Image URL is required")
        @Size(max = 1000, message = "Image URL must not exceed 1000 characters")
        String imageUrl,

        @Min(
                value = 1,
                message = "Display order must be at least 1"
        )
        Integer displayOrder

) {
}