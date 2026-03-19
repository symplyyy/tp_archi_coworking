package com.hotel.reservation_service.repository;

import com.hotel.reservation_service.model.Reservation;
import com.hotel.reservation_service.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByRoomId(Long roomId);

    long countByMemberIdAndStatus(Long memberId, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId " +
            "AND r.status = com.hotel.reservation_service.model.ReservationStatus.CONFIRMED " +
            "AND r.startDateTime < :endDateTime " +
            "AND r.endDateTime > :startDateTime")
    List<Reservation> findOverlappingReservations(
            @Param("roomId") Long roomId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    List<Reservation> findByRoomIdAndStatus(Long roomId, ReservationStatus status);

    List<Reservation> findByMemberIdAndStatus(Long memberId, ReservationStatus status);
}
