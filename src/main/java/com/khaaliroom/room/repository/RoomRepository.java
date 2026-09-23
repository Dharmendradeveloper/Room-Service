package com.khaaliroom.room.repository;

import com.khaaliroom.room.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface RoomRepository
        extends JpaRepository<Room, UUID>,
                JpaSpecificationExecutor<Room> {

    Page<Room> findByOwnerId(UUID ownerId, Pageable pageable);
}