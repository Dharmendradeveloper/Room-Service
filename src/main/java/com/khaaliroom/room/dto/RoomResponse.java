package com.khaaliroom.room.dto;

import com.khaaliroom.room.entity.FurnishingType;
import com.khaaliroom.room.entity.RoomStatus;
import com.khaaliroom.room.entity.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        UUID ownerId,
        String title,
        String description,
        BigDecimal monthlyRent,
        BigDecimal securityDeposit,
        String city,
        String locality,
        String address,
        RoomType roomType,
        FurnishingType furnishing,
        LocalDate availableFrom,
        RoomStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}