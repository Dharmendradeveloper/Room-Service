package com.khaaliroom.room.service;

import com.khaaliroom.room.dto.*;
import com.khaaliroom.room.entity.FurnishingType;
import com.khaaliroom.room.entity.Room;
import com.khaaliroom.room.entity.RoomStatus;
import com.khaaliroom.room.entity.RoomType;
import com.khaaliroom.room.exception.ResourceNotFoundException;
import com.khaaliroom.room.repository.RoomRepository;
import com.khaaliroom.room.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomResponse createRoom(UUID ownerId, RoomCreateRequest request) {

        Room room = Room.builder()
                .ownerId(ownerId)
                .title(request.title())
                .description(request.description())
                .monthlyRent(request.monthlyRent())
                .securityDeposit(request.securityDeposit())
                .city(request.city())
                .locality(request.locality())
                .address(request.address())
                .roomType(request.roomType())
                .furnishing(request.furnishing())
                .availableFrom(request.availableFrom())
                .status(RoomStatus.AVAILABLE)
                .build();

        Room savedRoom = roomRepository.save(room);

        return toResponse(savedRoom);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoom(UUID roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        return toResponse(room);
    }

    @Transactional
    public RoomResponse updateRoomStatus(
            UUID roomId,
            UUID authenticatedUserId,
            RoomStatusUpdateRequest request) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        if (!room.getOwnerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException(
                    "You are not authorized to modify this room"
            );
        }

        room.setStatus(request.status());

        Room updatedRoom = roomRepository.save(room);

        return toResponse(updatedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(
            UUID roomId,
            UUID authenticatedUserId,
            RoomUpdateRequest request) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        if (!room.getOwnerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException(
                    "You are not authorized to modify this room"
            );
        }

        room.setTitle(request.title());
        room.setDescription(request.description());
        room.setMonthlyRent(request.monthlyRent());
        room.setSecurityDeposit(request.securityDeposit());
        room.setCity(request.city());
        room.setLocality(request.locality());
        room.setAddress(request.address());
        room.setRoomType(request.roomType());
        room.setFurnishing(request.furnishing());
        room.setAvailableFrom(request.availableFrom());

        Room updatedRoom = roomRepository.save(room);

        return toResponse(updatedRoom);
    }

    @Transactional(readOnly = true)
    public RoomPageResponse findMyRooms(
            UUID ownerId,
            Pageable pageable) {

        Page<RoomResponse> roomPage =
                roomRepository
                        .findByOwnerId(ownerId, pageable)
                        .map(this::toResponse);

        return new RoomPageResponse(
                roomPage.getContent(),
                roomPage.getNumber(),
                roomPage.getSize(),
                roomPage.getTotalElements(),
                roomPage.getTotalPages(),
                roomPage.isFirst(),
                roomPage.isLast()
        );
    }

    private RoomResponse toResponse(Room room) {

        return new RoomResponse(
                room.getId(),
                room.getOwnerId(),
                room.getTitle(),
                room.getDescription(),
                room.getMonthlyRent(),
                room.getSecurityDeposit(),
                room.getCity(),
                room.getLocality(),
                room.getAddress(),
                room.getRoomType(),
                room.getFurnishing(),
                room.getAvailableFrom(),
                room.getStatus(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public RoomPageResponse searchAvailableRooms(
            String city,
            String locality,
            BigDecimal minRent,
            BigDecimal maxRent,
            RoomType roomType,
            FurnishingType furnishing,
            Pageable pageable) {

        Specification<Room> specification =
                RoomSpecification.hasStatus(RoomStatus.AVAILABLE);

        if (city != null && !city.isBlank()) {
            specification = specification.and(
                    RoomSpecification.hasCity(city)
            );
        }

        if (locality != null && !locality.isBlank()) {
            specification = specification.and(
                    RoomSpecification.hasLocality(locality)
            );
        }

        if (minRent != null) {
            specification = specification.and(
                    RoomSpecification.minimumRent(minRent)
            );
        }

        if (maxRent != null) {
            specification = specification.and(
                    RoomSpecification.maximumRent(maxRent)
            );
        }

        if (roomType != null) {
            specification = specification.and(
                    RoomSpecification.hasRoomType(roomType)
            );
        }

        if (furnishing != null) {
            specification = specification.and(
                    RoomSpecification.hasFurnishing(furnishing)
            );
        }

        Page<RoomResponse> roomPage =
                roomRepository
                        .findAll(specification, pageable)
                        .map(this::toResponse);

        return new RoomPageResponse(
                roomPage.getContent(),
                roomPage.getNumber(),
                roomPage.getSize(),
                roomPage.getTotalElements(),
                roomPage.getTotalPages(),
                roomPage.isFirst(),
                roomPage.isLast()
        );
    }
}