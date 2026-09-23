package com.khaaliroom.room.controller;

import com.khaaliroom.room.dto.*;
import com.khaaliroom.room.entity.FurnishingType;
import com.khaaliroom.room.entity.RoomType;
import com.khaaliroom.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            Authentication authentication,
            @Valid @RequestBody RoomCreateRequest request) {

        boolean isOwner = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_OWNER")
                );

        if (!isOwner) {
            throw new AccessDeniedException(
                    "Only room owners can create rooms"
            );
        }

        UUID ownerId = UUID.fromString(authentication.getName());

        RoomResponse response =
                roomService.createRoom(ownerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoom(
            @PathVariable UUID roomId) {

        RoomResponse response = roomService.getRoom(roomId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RoomPageResponse> getAvailableRooms(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) RoomType roomType,
            @RequestParam(required = false) FurnishingType furnishing,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be greater than or equal to 0"
            );
        }

        if (size < 1 || size > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Size must be between 1 and 50"
            );
        }

        if (!sortBy.equals("createdAt")
                && !sortBy.equals("monthlyRent")
                && !sortBy.equals("availableFrom")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortBy. Allowed values: createdAt, monthlyRent, availableFrom"
            );
        }

        Sort.Direction direction;

        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortDirection. Allowed values: asc, desc"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy)
        );

        RoomPageResponse rooms =
                roomService.searchAvailableRooms(
                        city,
                        locality,
                        minRent,
                        maxRent,
                        roomType,
                        furnishing,
                        pageable
                );

        return ResponseEntity.ok(rooms);
    }

    @PatchMapping("/{roomId}/status")
    public ResponseEntity<RoomResponse> updateRoomStatus(
            @PathVariable UUID roomId,
            Authentication authentication,
            @Valid @RequestBody RoomStatusUpdateRequest request) {

        boolean isOwner = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_OWNER")
                );

        if (!isOwner) {
            throw new AccessDeniedException(
                    "Only room owners can change room status"
            );
        }

        UUID authenticatedUserId =
                UUID.fromString(authentication.getName());

        RoomResponse response =
                roomService.updateRoomStatus(
                        roomId,
                        authenticatedUserId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable UUID roomId,
            Authentication authentication,
            @Valid @RequestBody RoomUpdateRequest request) {

        boolean isOwner = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_OWNER")
                );

        if (!isOwner) {
            throw new AccessDeniedException(
                    "Only room owners can update rooms"
            );
        }

        UUID authenticatedUserId =
                UUID.fromString(authentication.getName());

        RoomResponse response =
                roomService.updateRoom(
                        roomId,
                        authenticatedUserId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<RoomPageResponse> getMyRooms(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be greater than or equal to 0"
            );
        }

        if (size < 1 || size > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Size must be between 1 and 50"
            );
        }

        if (!sortBy.equals("createdAt")
                && !sortBy.equals("monthlyRent")
                && !sortBy.equals("availableFrom")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortBy. Allowed values: createdAt, monthlyRent, availableFrom"
            );
        }

        Sort.Direction direction;

        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortDirection. Allowed values: asc, desc"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy)
        );

        UUID ownerId = UUID.fromString(authentication.getName());

        RoomPageResponse rooms =
                roomService.findMyRooms(ownerId, pageable);

        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable UUID roomId,
            Authentication authentication) {

        boolean isOwner = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_OWNER")
                );

        if (!isOwner) {
            throw new AccessDeniedException(
                    "Only room owners can delete rooms"
            );
        }

        UUID authenticatedUserId =
                UUID.fromString(authentication.getName());

        roomService.deleteRoom(
                roomId,
                authenticatedUserId
        );

        return ResponseEntity.noContent().build();
    }
}