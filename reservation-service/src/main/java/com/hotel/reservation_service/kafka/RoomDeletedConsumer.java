package com.hotel.reservation_service.kafka;

import com.hotel.reservation_service.model.Reservation;
import com.hotel.reservation_service.model.ReservationStatus;
import com.hotel.reservation_service.repository.ReservationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomDeletedConsumer {

    private final ReservationRepository reservationRepository;

    public RoomDeletedConsumer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @KafkaListener(topics = "room-deleted", groupId = "reservation-service")
    public void handleRoomDeleted(String message) {
        Long roomId = Long.parseLong(message.trim());
        List<Reservation> confirmed = reservationRepository.findByRoomIdAndStatus(roomId, ReservationStatus.CONFIRMED);
        confirmed.forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
        reservationRepository.saveAll(confirmed);
    }
}
