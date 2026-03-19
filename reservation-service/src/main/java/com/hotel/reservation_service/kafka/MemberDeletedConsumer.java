package com.hotel.reservation_service.kafka;

import com.hotel.reservation_service.repository.ReservationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MemberDeletedConsumer {

    private final ReservationRepository reservationRepository;

    public MemberDeletedConsumer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @KafkaListener(topics = "member-deleted", groupId = "reservation-service")
    public void handleMemberDeleted(String message) {
        Long memberId = Long.parseLong(message.trim());
        reservationRepository.deleteAll(reservationRepository.findByMemberId(memberId));
    }
}
