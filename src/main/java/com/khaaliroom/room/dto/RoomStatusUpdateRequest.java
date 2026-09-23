package com.khaaliroom.room.dto;

import com.khaaliroom.room.entity.RoomStatus;
import jakarta.validation.constraints.NotNull;

public record RoomStatusUpdateRequest(

        @NotNull(message = "Room status is required")
        RoomStatus status

) {
}