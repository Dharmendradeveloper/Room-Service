package com.khaaliroom.room.specification;

import com.khaaliroom.room.entity.FurnishingType;
import com.khaaliroom.room.entity.Room;
import com.khaaliroom.room.entity.RoomStatus;
import com.khaaliroom.room.entity.RoomType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class RoomSpecification {

    private RoomSpecification() {
    }

    public static Specification<Room> hasStatus(RoomStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Room> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("city")),
                        city.toLowerCase()
                );
    }

    public static Specification<Room> hasLocality(String locality) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("locality")),
                        locality.toLowerCase()
                );
    }

    public static Specification<Room> minimumRent(BigDecimal minRent) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("monthlyRent"),
                        minRent
                );
    }

    public static Specification<Room> maximumRent(BigDecimal maxRent) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("monthlyRent"),
                        maxRent
                );
    }

    public static Specification<Room> hasRoomType(RoomType roomType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("roomType"), roomType);
    }

    public static Specification<Room> hasFurnishing(FurnishingType furnishing) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("furnishing"), furnishing);
    }
}