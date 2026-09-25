package com.khaaliroom.room.service;

import com.khaaliroom.room.dto.RoomImageCreateRequest;
import com.khaaliroom.room.dto.RoomImageResponse;
import com.khaaliroom.room.entity.Room;
import com.khaaliroom.room.entity.RoomImage;
import com.khaaliroom.room.exception.ResourceNotFoundException;
import com.khaaliroom.room.repository.RoomImageRepository;
import com.khaaliroom.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomImageService {

    private final RoomImageRepository roomImageRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<RoomImageResponse> getRoomImages(UUID roomId) {

        return roomImageRepository
                .findByRoomIdOrderByDisplayOrderAsc(roomId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RoomImageResponse createRoomImage(
            UUID roomId,
            UUID authenticatedUserId,
            RoomImageCreateRequest request) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        if (!room.getOwnerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException(
                    "You are not authorized to add images to this room"
            );
        }

        RoomImage image = RoomImage.builder()
                .roomId(roomId)
                .imageUrl(request.imageUrl())
                .displayOrder(request.displayOrder())
                .build();

        RoomImage savedImage =
                roomImageRepository.save(image);

        return toResponse(savedImage);
    }

    private RoomImageResponse toResponse(RoomImage image) {

        return new RoomImageResponse(
                image.getId(),
                image.getRoomId(),
                image.getImageUrl(),
                image.getDisplayOrder(),
                image.getCreatedAt()
        );
    }
}