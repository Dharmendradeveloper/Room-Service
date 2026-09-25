package com.khaaliroom.room.dto;

import com.khaaliroom.room.entity.FurnishingType;
import com.khaaliroom.room.entity.RoomStatus;
import com.khaaliroom.room.entity.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RoomResponse(

        @Schema(
                description = "Unique identifier of the room listing",
                example = "1dc1071f-ccc3-4b26-a598-63ab37293658"
        )
        UUID id,

        @Schema(
                description = "Unique identifier of the room owner",
                example = "a6524c06-7270-4070-99d1-36f5a674b1cb"
        )
        UUID ownerId,

        @Schema(
                description = "Title of the room listing",
                example = "Private room near college"
        )
        String title,

        @Schema(
                description = "Detailed description of the room",
                example = "Fully furnished private room suitable for a student"
        )
        String description,

        @Schema(
                description = "Monthly rent in INR",
                example = "10000"
        )
        BigDecimal monthlyRent,

        @Schema(
                description = "Security deposit in INR",
                example = "20000"
        )
        BigDecimal securityDeposit,

        @Schema(
                description = "City where the room is located",
                example = "Pune"
        )
        String city,

        @Schema(
                description = "Locality or neighborhood where the room is located",
                example = "Charholi"
        )
        String locality,

        @Schema(
                description = "Full address of the room",
                example = "College Road, Charholi, Pune"
        )
        String address,

        @Schema(
                description = "Type of room or accommodation",
                example = "PRIVATE_ROOM"
        )
        RoomType roomType,

        @Schema(
                description = "Furnishing level of the room",
                example = "FULLY_FURNISHED"
        )
        FurnishingType furnishing,

        @Schema(
                description = "Date from which the room is available",
                example = "2026-10-01"
        )
        LocalDate availableFrom,


        @Schema(
                description = """
                        Current status of the room listing.
                        AVAILABLE = Room is currently available for rent.
                        FILLED = Room has been occupied by a tenant.
                        DISABLED = Listing is temporarily hidden or inactive.
                        """,
                example = "AVAILABLE"
        )
        RoomStatus status,

        @Schema(
                description = "Date and time when the room listing was created",
                example = "2026-09-24T08:32:55"
        )
        LocalDateTime createdAt,

        @Schema(
                description = "Date and time when the room listing was last updated",
                example = "2026-09-24T08:33:19"
        )
        LocalDateTime updatedAt
) {
}