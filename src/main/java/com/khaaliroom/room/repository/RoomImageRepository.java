package com.khaaliroom.room.repository;

import com.khaaliroom.room.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomImageRepository
        extends JpaRepository<RoomImage, UUID> {

    List<RoomImage> findByRoomIdOrderByDisplayOrderAsc(UUID roomId);

    long countByRoomId(UUID roomId);

    boolean existsByRoomIdAndDisplayOrder(
            UUID roomId,
            Integer displayOrder
    );

    Optional<RoomImage> findByIdAndRoomId(
            UUID imageId,
            UUID roomId
    );
}