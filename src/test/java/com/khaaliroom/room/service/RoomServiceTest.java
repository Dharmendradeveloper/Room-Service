package com.khaaliroom.room.service;

import com.khaaliroom.room.dto.*;
import com.khaaliroom.room.entity.FurnishingType;
import com.khaaliroom.room.entity.Room;
import com.khaaliroom.room.entity.RoomType;
import com.khaaliroom.room.entity.RoomStatus;
import com.khaaliroom.room.exception.ResourceNotFoundException;
import com.khaaliroom.room.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private UUID roomId;
    private UUID ownerId;
    private Room room;

    @BeforeEach
    void setUp() {

        roomId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        room = Room.builder()
                .id(roomId)
                .ownerId(ownerId)
                .title("Original Room")
                .description("Original description")
                .monthlyRent(new BigDecimal("10000"))
                .securityDeposit(new BigDecimal("20000"))
                .city("Pune")
                .locality("Charholi")
                .address("College Road")
                .roomType(RoomType.PRIVATE_ROOM)
                .furnishing(FurnishingType.FULLY_FURNISHED)
                .availableFrom(LocalDate.of(2026, 10, 1))
                .status(RoomStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void updateRoom_shouldUpdateRoomWhenAuthenticatedUserIsOwner() {

        RoomUpdateRequest request = new RoomUpdateRequest(
                "Updated Room",
                "Updated description",
                new BigDecimal("11000"),
                new BigDecimal("22000"),
                "Pune",
                "Charholi",
                "Updated College Road",
                RoomType.PRIVATE_ROOM,
                FurnishingType.FULLY_FURNISHED,
                LocalDate.of(2026, 10, 1)
        );

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        when(roomRepository.save(any(Room.class)))
                .thenReturn(room);

        RoomResponse response =
                roomService.updateRoom(
                        roomId,
                        ownerId,
                        request
                );

        assertNotNull(response);
        assertEquals("Updated Room", response.title());
        assertEquals(
                new BigDecimal("11000"),
                response.monthlyRent()
        );
        assertEquals(
                new BigDecimal("22000"),
                response.securityDeposit()
        );
        assertEquals("Updated description", response.description());
        assertEquals("Updated College Road", response.address());

        verify(roomRepository).findById(roomId);
        verify(roomRepository).save(room);
    }

    @Test
    void updateRoom_shouldThrowAccessDeniedWhenAuthenticatedUserIsNotOwner() {

        UUID anotherUserId = UUID.randomUUID();

        RoomUpdateRequest request = new RoomUpdateRequest(
                "Updated Room",
                "Updated description",
                new BigDecimal("11000"),
                new BigDecimal("22000"),
                "Pune",
                "Charholi",
                "Updated College Road",
                RoomType.PRIVATE_ROOM,
                FurnishingType.FULLY_FURNISHED,
                LocalDate.of(2026, 10, 1)
        );

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> roomService.updateRoom(
                        roomId,
                        anotherUserId,
                        request
                )
        );

        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).save(any(Room.class));
    }
    @Test
    void updateRoom_shouldThrowResourceNotFoundWhenRoomDoesNotExist() {

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.empty());

        RoomUpdateRequest request = new RoomUpdateRequest(
                "Updated Room",
                "Updated description",
                new BigDecimal("11000"),
                new BigDecimal("22000"),
                "Pune",
                "Charholi",
                "Updated College Road",
                RoomType.PRIVATE_ROOM,
                FurnishingType.FULLY_FURNISHED,
                LocalDate.of(2026, 10, 1)
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.updateRoom(
                        roomId,
                        ownerId,
                        request
                )
        );

        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void updateRoomStatus_shouldAllowAvailableToFilled() {

        RoomStatusUpdateRequest request =
                new RoomStatusUpdateRequest(RoomStatus.FILLED);

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        when(roomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RoomResponse response =
                roomService.updateRoomStatus(
                        roomId,
                        ownerId,
                        request
                );

        assertNotNull(response);
        assertEquals(RoomStatus.FILLED, response.status());
        assertEquals(RoomStatus.FILLED, room.getStatus());

        verify(roomRepository).findById(roomId);
        verify(roomRepository).save(room);
    }

    @Test
    void updateRoomStatus_shouldRejectFilledToDisabled() {

        room.setStatus(RoomStatus.FILLED);

        RoomStatusUpdateRequest request =
                new RoomStatusUpdateRequest(RoomStatus.DISABLED);

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> roomService.updateRoomStatus(
                        roomId,
                        ownerId,
                        request
                )
        );

        assertEquals(RoomStatus.FILLED, room.getStatus());

        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @ParameterizedTest
    @CsvSource({
            "DISABLED, AVAILABLE",
            "FILLED, AVAILABLE",
            "AVAILABLE, DISABLED"
    })
    void updateRoomStatus_shouldAllowValidTransitions(
            RoomStatus currentStatus,
            RoomStatus newStatus) {

        room.setStatus(currentStatus);

        RoomStatusUpdateRequest request =
                new RoomStatusUpdateRequest(newStatus);

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        when(roomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RoomResponse response =
                roomService.updateRoomStatus(
                        roomId,
                        ownerId,
                        request
                );

        assertNotNull(response);
        assertEquals(newStatus, response.status());
        assertEquals(newStatus, room.getStatus());

        verify(roomRepository).findById(roomId);
        verify(roomRepository).save(room);

        reset(roomRepository);
    }

    @Test
    void updateRoomStatus_shouldRejectDisabledToFilled() {

        room.setStatus(RoomStatus.DISABLED);

        RoomStatusUpdateRequest request =
                new RoomStatusUpdateRequest(RoomStatus.FILLED);

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> roomService.updateRoomStatus(
                        roomId,
                        ownerId,
                        request
                )
        );

        assertEquals(RoomStatus.DISABLED, room.getStatus());

        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void deleteRoom_shouldDeleteRoomWhenAuthenticatedUserIsOwner() {

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        roomService.deleteRoom(roomId, ownerId);

        verify(roomRepository).findById(roomId);
        verify(roomRepository).delete(room);
    }

    @Test
    void deleteRoom_shouldThrowAccessDeniedWhenAuthenticatedUserIsNotOwner() {

        UUID anotherUserId = UUID.randomUUID();

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> roomService.deleteRoom(
                        roomId,
                        anotherUserId
                )
        );

        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).delete(any(Room.class));
    }

    @Test
    void deleteRoom_shouldThrowResourceNotFoundWhenRoomDoesNotExist() {

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.deleteRoom(
                        roomId,
                        ownerId
                )
        );

        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).delete(any(Room.class));
    }

    @Test
    void createRoom_shouldCreateRoomWithAvailableStatus() {

        RoomCreateRequest request = new RoomCreateRequest(
                "Private room near college",
                "Fully furnished private room",
                new BigDecimal("10000"),
                new BigDecimal("20000"),
                "Pune",
                "Charholi",
                "College Road, Charholi",
                RoomType.PRIVATE_ROOM,
                FurnishingType.FULLY_FURNISHED,
                LocalDate.of(2026, 10, 1)
        );

        when(roomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> {
                    Room savedRoom = invocation.getArgument(0);

                    savedRoom.setId(roomId);
                    savedRoom.setCreatedAt(LocalDateTime.now());
                    savedRoom.setUpdatedAt(LocalDateTime.now());

                    return savedRoom;
                });

        RoomResponse response =
                roomService.createRoom(ownerId, request);

        assertNotNull(response);
        assertEquals(roomId, response.id());
        assertEquals(ownerId, response.ownerId());
        assertEquals("Private room near college", response.title());
        assertEquals(
                new BigDecimal("10000"),
                response.monthlyRent()
        );
        assertEquals(
                RoomStatus.AVAILABLE,
                response.status()
        );

        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void getRoom_shouldReturnRoomWhenRoomExists() {

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.of(room));

        RoomResponse response =
                roomService.getRoom(roomId);

        assertNotNull(response);
        assertEquals(roomId, response.id());
        assertEquals(ownerId, response.ownerId());
        assertEquals("Original Room", response.title());
        assertEquals(
                RoomStatus.AVAILABLE,
                response.status()
        );

        verify(roomRepository).findById(roomId);
    }

    @Test
    void getRoom_shouldThrowResourceNotFoundWhenRoomDoesNotExist() {

        when(roomRepository.findById(roomId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.getRoom(roomId)
        );

        verify(roomRepository).findById(roomId);
    }

    @Test
    void searchAvailableRooms_shouldReturnAvailableRooms() {

        Room availableRoom = room;

        availableRoom.setStatus(RoomStatus.AVAILABLE);

        RoomResponse availableResponse = new RoomResponse(
                availableRoom.getId(),
                availableRoom.getOwnerId(),
                availableRoom.getTitle(),
                availableRoom.getDescription(),
                availableRoom.getMonthlyRent(),
                availableRoom.getSecurityDeposit(),
                availableRoom.getCity(),
                availableRoom.getLocality(),
                availableRoom.getAddress(),
                availableRoom.getRoomType(),
                availableRoom.getFurnishing(),
                availableRoom.getAvailableFrom(),
                availableRoom.getStatus(),
                availableRoom.getCreatedAt(),
                availableRoom.getUpdatedAt()
        );

        PageImpl<Room> page =
                new PageImpl<>(
                        java.util.List.of(availableRoom),
                        PageRequest.of(0, 10),
                        1
                );

        when(roomRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(org.springframework.data.domain.Pageable.class)
        )).thenReturn(page);

        RoomPageResponse response =
                roomService.searchAvailableRooms(
                        "Pune",
                        "Charholi",
                        new BigDecimal("5000"),
                        new BigDecimal("15000"),
                        RoomType.PRIVATE_ROOM,
                        FurnishingType.FULLY_FURNISHED,
                        PageRequest.of(0, 10)
                );

        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals(
                RoomStatus.AVAILABLE,
                response.content().get(0).status()
        );
        assertEquals("Pune", response.content().get(0).city());
        assertEquals("Charholi", response.content().get(0).locality());

        verify(roomRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(org.springframework.data.domain.Pageable.class)
        );
    }

    @Test
    void findMyRooms_shouldReturnRoomsOwnedByUser() {

        PageImpl<Room> page =
                new PageImpl<>(
                        java.util.List.of(room),
                        PageRequest.of(0, 10),
                        1
                );

        when(roomRepository.findByOwnerId(
                eq(ownerId),
                any(org.springframework.data.domain.Pageable.class)
        )).thenReturn(page);

        RoomPageResponse response =
                roomService.findMyRooms(
                        ownerId,
                        PageRequest.of(0, 10)
                );

        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());
        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertTrue(response.first());
        assertTrue(response.last());

        assertEquals(
                ownerId,
                response.content().get(0).ownerId()
        );

        verify(roomRepository).findByOwnerId(
                eq(ownerId),
                any(org.springframework.data.domain.Pageable.class)
        );
    }
}