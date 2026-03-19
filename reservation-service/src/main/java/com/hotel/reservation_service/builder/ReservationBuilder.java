package com.hotel.reservation_service.builder;

import com.hotel.reservation_service.model.Reservation;
import com.hotel.reservation_service.model.ReservationStatus;

import java.time.LocalDateTime;

public class ReservationBuilder {

    private Long roomId;
    private Long memberId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    public ReservationBuilder roomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public ReservationBuilder memberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public ReservationBuilder startDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public ReservationBuilder endDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

    public ReservationBuilder status(ReservationStatus status) {
        this.status = status;
        return this;
    }

    public Reservation build() {
        if (roomId == null || memberId == null || startDateTime == null || endDateTime == null) {
            throw new IllegalStateException("roomId, memberId, startDateTime et endDateTime sont obligatoires.");
        }
        if (!startDateTime.isBefore(endDateTime)) {
            throw new IllegalStateException("La date de début doit être antérieure à la date de fin.");
        }
        Reservation reservation = new Reservation();
        reservation.setRoomId(roomId);
        reservation.setMemberId(memberId);
        reservation.setStartDateTime(startDateTime);
        reservation.setEndDateTime(endDateTime);
        reservation.setStatus(status);
        return reservation;
    }
}
