package com.khaaliroom.room.dto;

import com.khaaliroom.room.entity.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RoomStatusUpdateRequest(

        @NotNull(message = "Room status is required")
        @Schema(
                description = """
                New status for the room listing.
                AVAILABLE = Room is currently available for rent.
                FILLED = Room has been occupied by a tenant.
                DISABLED = Listing is temporarily hidden or inactive.
                """,
                example = "FILLED"
        )
        RoomStatus status

) {
}