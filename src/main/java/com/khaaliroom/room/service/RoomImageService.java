package com.khaaliroom.room.service;

import com.khaaliroom.room.dto.RoomImageCompleteRequest;
import com.khaaliroom.room.dto.RoomImageResponse;
import com.khaaliroom.room.entity.Room;
import com.khaaliroom.room.entity.RoomImage;
import com.khaaliroom.room.exception.ResourceNotFoundException;
import com.khaaliroom.room.repository.RoomImageRepository;
import com.khaaliroom.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomImageService {

    private final RoomImageRepository roomImageRepository;
    private final RoomRepository roomRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<RoomImageResponse> getRoomImages(UUID roomId) {

        return roomImageRepository
                .findByRoomIdOrderByDisplayOrderAsc(roomId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RoomImageResponse toResponse(RoomImage image) {

        String downloadUrl =
                s3Service.generateDownloadUrl(
                        image.getObjectKey()
                );

        return new RoomImageResponse(
                image.getId(),
                image.getRoomId(),
                downloadUrl,
                image.getDisplayOrder(),
                image.getCreatedAt()
        );
    }

    @Transactional
    public RoomImageResponse completeImageUpload(
            UUID roomId,
            UUID authenticatedUserId,
            RoomImageCompleteRequest request) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        if (!room.getOwnerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException(
                    "You are not authorized to add images to this room"
            );
        }

        String objectKey =
                findObjectKey(roomId, request.imageId());

        long imageCount =
                roomImageRepository.countByRoomId(roomId);

        if (imageCount >= 4) {

            s3Service.deleteObject(objectKey);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A room can have a maximum of 4 images"
            );
        }

        if (roomImageRepository.existsByRoomIdAndDisplayOrder(
                roomId,
                request.displayOrder())) {

            s3Service.deleteObject(objectKey);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Display order "
                            + request.displayOrder()
                            + " is already in use"
            );
        }

        String contentType =
                s3Service.getObjectContentType(objectKey);

        if (!List.of(
                "image/jpeg",
                "image/png",
                "image/webp"
        ).contains(contentType)) {

            s3Service.deleteObject(objectKey);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported image content type"
            );
        }

        try {
            s3Service.validateImageSize(objectKey);

        } catch (NoSuchKeyException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uploaded image was not found in S3"
            );

        } catch (software.amazon.awssdk.services.s3.model.S3Exception exception) {

            if (exception.statusCode() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Uploaded image was not found in S3"
                );
            }

            throw exception;
        }

        RoomImage image = RoomImage.builder()
                .roomId(roomId)
                .objectKey(objectKey)
                .contentType(contentType)
                .displayOrder(request.displayOrder())
                .build();

        RoomImage savedImage =
                roomImageRepository.save(image);

        return toResponse(savedImage);
    }

    private String findObjectKey(
            UUID roomId,
            UUID imageId) {

        String baseKey =
                "rooms/" + roomId + "/" + imageId;

        String[] extensions = {
                ".jpg",
                ".png",
                ".webp"
        };

        for (String extension : extensions) {

            String objectKey = baseKey + extension;

            try {
                s3Service.getObjectMetadata(objectKey);
                return objectKey;

            } catch (NoSuchKeyException ignored) {
                // Try the next extension.
            } catch (software.amazon.awssdk.services.s3.model.S3Exception exception) {

                if (exception.statusCode() != 404) {
                    throw exception;
                }
            }
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Uploaded image was not found in S3"
        );
    }

    @Transactional
    public void deleteRoomImage(
            UUID roomId,
            UUID imageId,
            UUID authenticatedUserId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        if (!room.getOwnerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException(
                    "You are not authorized to delete images from this room"
            );
        }

        RoomImage image =
                roomImageRepository.findByIdAndRoomId(
                                imageId,
                                roomId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Image not found"));

        s3Service.deleteObject(image.getObjectKey());

        roomImageRepository.delete(image);
    }
}