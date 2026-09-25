package com.khaaliroom.room.service;

import com.khaaliroom.room.dto.RoomImageResponse;
import com.khaaliroom.room.entity.RoomImage;
import com.khaaliroom.room.repository.RoomImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomImageServiceTest {

    @Mock
    private RoomImageRepository roomImageRepository;

    @InjectMocks
    private RoomImageService roomImageService;

    private UUID roomId;
    private RoomImage firstImage;
    private RoomImage secondImage;

    @BeforeEach
    void setUp() {

        roomId = UUID.randomUUID();

        firstImage = RoomImage.builder()
                .id(UUID.randomUUID())
                .roomId(roomId)
                .imageUrl("https://example.com/image-1.jpg")
                .displayOrder(1)
                .createdAt(LocalDateTime.now())
                .build();

        secondImage = RoomImage.builder()
                .id(UUID.randomUUID())
                .roomId(roomId)
                .imageUrl("https://example.com/image-2.jpg")
                .displayOrder(2)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getRoomImages_shouldReturnImagesInDisplayOrder() {

        when(roomImageRepository
                .findByRoomIdOrderByDisplayOrderAsc(roomId))
                .thenReturn(List.of(firstImage, secondImage));

        List<RoomImageResponse> response =
                roomImageService.getRoomImages(roomId);

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(
                firstImage.getId(),
                response.get(0).id()
        );
        assertEquals(
                1,
                response.get(0).displayOrder()
        );

        assertEquals(
                secondImage.getId(),
                response.get(1).id()
        );
        assertEquals(
                2,
                response.get(1).displayOrder()
        );

        verify(roomImageRepository)
                .findByRoomIdOrderByDisplayOrderAsc(roomId);
    }

    @Test
    void getRoomImages_shouldReturnEmptyListWhenRoomHasNoImages() {

        when(roomImageRepository
                .findByRoomIdOrderByDisplayOrderAsc(roomId))
                .thenReturn(List.of());

        List<RoomImageResponse> response =
                roomImageService.getRoomImages(roomId);

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(roomImageRepository)
                .findByRoomIdOrderByDisplayOrderAsc(roomId);
    }
}