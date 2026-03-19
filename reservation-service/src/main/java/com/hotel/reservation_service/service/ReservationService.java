package com.hotel.reservation_service.service;

import com.hotel.reservation_service.dto.MemberDto;
import com.hotel.reservation_service.dto.ReservationRequest;
import com.hotel.reservation_service.dto.RoomDto;
import com.hotel.reservation_service.exception.BookingException;
import com.hotel.reservation_service.exception.ResourceNotFoundException;
import com.hotel.reservation_service.kafka.MemberSuspensionProducer;
import com.hotel.reservation_service.model.Reservation;
import com.hotel.reservation_service.model.ReservationStatus;
import com.hotel.reservation_service.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestTemplate restTemplate;
    private final MemberSuspensionProducer suspensionProducer;

    @Value("${services.room-service.url}")
    private String roomServiceUrl;

    @Value("${services.member-service.url}")
    private String memberServiceUrl;

    public ReservationService(ReservationRepository reservationRepository,
                              RestTemplate restTemplate,
                              MemberSuspensionProducer suspensionProducer) {
        this.reservationRepository = reservationRepository;
        this.restTemplate = restTemplate;
        this.suspensionProducer = suspensionProducer;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable : " + id));
    }

    public Reservation createReservation(ReservationRequest request) {
        RoomDto room = fetchRoom(request.getRoomId());
        if (!room.isAvailable()) {
            throw new BookingException("La salle n'est pas disponible.");
        }

        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                request.getRoomId(), request.getStartDateTime(), request.getEndDateTime()
        );
        if (!overlapping.isEmpty()) {
            throw new BookingException("La salle est déjà réservée sur ce créneau.");
        }

        MemberDto member = fetchMember(request.getMemberId());
        if (member.isSuspended()) {
            throw new BookingException("Le membre est suspendu et ne peut plus effectuer de réservation.");
        }

        Reservation reservation = new Reservation();
        reservation.setRoomId(request.getRoomId());
        reservation.setMemberId(request.getMemberId());
        reservation.setStartDateTime(request.getStartDateTime());
        reservation.setEndDateTime(request.getEndDateTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation saved = reservationRepository.save(reservation);

        updateRoomAvailability(request.getRoomId(), false);

        long activeCount = reservationRepository.countByMemberIdAndStatus(request.getMemberId(), ReservationStatus.CONFIRMED);
        if (activeCount >= member.getMaxConcurrentBookings()) {
            suspensionProducer.publishSuspensionUpdate(request.getMemberId(), true);
        }

        return saved;
    }

    public Reservation cancelReservation(Long id) {
        Reservation reservation = getReservationById(id);
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BookingException("Seule une réservation confirmée peut être annulée.");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation saved = reservationRepository.save(reservation);

        updateRoomAvailability(reservation.getRoomId(), true);
        checkAndUnsuspendMember(reservation.getMemberId());

        return saved;
    }

    public Reservation completeReservation(Long id) {
        Reservation reservation = getReservationById(id);
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BookingException("Seule une réservation confirmée peut être marquée comme terminée.");
        }
        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation saved = reservationRepository.save(reservation);

        updateRoomAvailability(reservation.getRoomId(), true);
        checkAndUnsuspendMember(reservation.getMemberId());

        return saved;
    }

    public List<Reservation> getByMember(Long memberId) {
        return reservationRepository.findByMemberId(memberId);
    }

    public List<Reservation> getByRoom(Long roomId) {
        return reservationRepository.findByRoomId(roomId);
    }

    private RoomDto fetchRoom(Long roomId) {
        try {
            return restTemplate.getForObject(roomServiceUrl + "/rooms/" + roomId, RoomDto.class);
        } catch (HttpClientErrorException e) {
            throw new ResourceNotFoundException("Salle introuvable : " + roomId);
        }
    }

    private MemberDto fetchMember(Long memberId) {
        try {
            return restTemplate.getForObject(memberServiceUrl + "/members/" + memberId, MemberDto.class);
        } catch (HttpClientErrorException e) {
            throw new ResourceNotFoundException("Membre introuvable : " + memberId);
        }
    }

    private void updateRoomAvailability(Long roomId, boolean available) {
        restTemplate.patchForObject(
                roomServiceUrl + "/rooms/" + roomId + "/availability?available=" + available,
                null,
                RoomDto.class
        );
    }

    private void checkAndUnsuspendMember(Long memberId) {
        try {
            MemberDto member = fetchMember(memberId);
            if (member.isSuspended()) {
                long activeCount = reservationRepository.countByMemberIdAndStatus(memberId, ReservationStatus.CONFIRMED);
                if (activeCount < member.getMaxConcurrentBookings()) {
                    suspensionProducer.publishSuspensionUpdate(memberId, false);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
