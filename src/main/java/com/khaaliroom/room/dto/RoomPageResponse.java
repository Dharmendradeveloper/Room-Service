package com.khaaliroom.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record RoomPageResponse(

        @Schema(
                description = "List of room listings returned for the current page"
        )
        List<RoomResponse> content,

        @Schema(
                description = "Current page number, starting from 0",
                example = "0"
        )
        int page,

        @Schema(
                description = "Number of rooms requested per page",
                example = "10"
        )
        int size,

        @Schema(
                description = "Total number of room listings matching the search criteria",
                example = "25"
        )
        long totalElements,

        @Schema(
                description = "Total number of pages available",
                example = "3"
        )
        int totalPages,

        @Schema(
                description = "Whether this is the first page",
                example = "true"
        )
        boolean first,

        @Schema(
                description = "Whether this is the last page",
                example = "false"
        )
        boolean last
) {
}