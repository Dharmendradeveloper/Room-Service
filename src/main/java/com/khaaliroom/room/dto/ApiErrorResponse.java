package com.khaaliroom.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Standard API error response")
public record ApiErrorResponse(

        @Schema(
                description = "Date and time when the error occurred",
                example = "2026-09-24T09:15:30"
        )
        LocalDateTime timestamp,

        @Schema(
                description = "HTTP status code",
                example = "404"
        )
        int status,

        @Schema(
                description = "Short description of the error type",
                example = "Not Found"
        )
        String error,

        @Schema(
                description = "Detailed error message",
                example = "Room not found with id: 1dc1071f-ccc3-4b26-a598-63ab37293658"
        )
        String message,

        @Schema(
                description = "API endpoint where the error occurred",
                example = "/api/v1/rooms/1dc1071f-ccc3-4b26-a598-63ab37293658"
        )
        String path,

        @Schema(
                description = "Field-level validation errors. Present only when request validation fails.",
                example = "{\"title\":\"Title is required\",\"monthlyRent\":\"Monthly rent must be greater than 0\"}"
        )
        Map<String, String> validationErrors
) {
}