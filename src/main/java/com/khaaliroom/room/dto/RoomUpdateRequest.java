package com.khaaliroom.room.dto;

import com.khaaliroom.room.entity.FurnishingType;
import com.khaaliroom.room.entity.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RoomUpdateRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        @Schema(
                description = "Title of the room listing",
                example = "Updated private room near college"
        )
        String title,

        @Size(max = 3000, message = "Description must not exceed 3000 characters")
        @Schema(
                description = "Detailed description of the room and accommodation",
                example = "Fully furnished private room with attached bathroom"
        )
        String description,

        @NotNull(message = "Monthly rent is required")
        @DecimalMin(
                value = "0.0",
                inclusive = false,
                message = "Monthly rent must be greater than 0"
        )
        @Schema(
                description = "Monthly rent in INR",
                example = "11000"
        )
        BigDecimal monthlyRent,

        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Security deposit cannot be negative"
        )
        @Schema(
                description = "Security deposit in INR",
                example = "22000"
        )
        BigDecimal securityDeposit,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        @Schema(
                description = "City where the room is located",
                example = "Pune"
        )
        String city,

        @NotBlank(message = "Locality is required")
        @Size(max = 150, message = "Locality must not exceed 150 characters")
        @Schema(
                description = "Locality or neighborhood where the room is located",
                example = "Charholi"
        )
        String locality,

        @Size(max = 500, message = "Address must not exceed 500 characters")
        @Schema(
                description = "Full address of the room",
                example = "College Road, Charholi, Pune"
        )
        String address,

        @NotNull(message = "Room type is required")
        @Schema(
                description = """
                Type of room or accommodation.
                PRIVATE_ROOM = Private room for one tenant.
                SHARED_ROOM = Room shared by multiple tenants.
                ENTIRE_FLAT = Entire flat rented by the user.
                PG = Paying Guest accommodation.
                """,
                example = "PRIVATE_ROOM"
        )
        RoomType roomType,

        @NotNull(message = "Furnishing type is required")
        @Schema(
                description = """
                Furnishing level of the room.
                UNFURNISHED = Room without furniture.
                SEMI_FURNISHED = Room with some basic furniture.
                FULLY_FURNISHED = Room with complete furniture and essential amenities.
                """,
                example = "FULLY_FURNISHED"
        )
        FurnishingType furnishing,

        @FutureOrPresent(
                message = "Available from date cannot be in the past"
        )
        @Schema(
                description = "Date from which the room is available",
                example = "2026-10-01"
        )
        LocalDate availableFrom
) {
}