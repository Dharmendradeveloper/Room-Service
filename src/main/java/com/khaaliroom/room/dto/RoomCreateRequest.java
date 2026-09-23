package com.khaaliroom.room.dto;

import com.khaaliroom.room.entity.FurnishingType;
import com.khaaliroom.room.entity.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RoomCreateRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @Size(max = 3000, message = "Description must not exceed 3000 characters")
        String description,

        @NotNull(message = "Monthly rent is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Monthly rent must be greater than 0")
        BigDecimal monthlyRent,

        @DecimalMin(value = "0.0", inclusive = true, message = "Security deposit cannot be negative")
        BigDecimal securityDeposit,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @NotBlank(message = "Locality is required")
        @Size(max = 150, message = "Locality must not exceed 150 characters")
        String locality,

        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        @NotNull(message = "Room type is required")
        RoomType roomType,

        @NotNull(message = "Furnishing type is required")
        FurnishingType furnishing,

        @FutureOrPresent(message = "Available from date cannot be in the past")
        LocalDate availableFrom
) {
}