package com.hotel.reservation_service.controller;

import com.hotel.reservation_service.dto.ReservationRequest;
import com.hotel.reservation_service.model.Reservation;
import com.hotel.reservation_service.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Réservations", description = "Gestion des réservations de salles")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Lister toutes les réservations")
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @Operation(summary = "Récupérer une réservation par son id")
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @Operation(summary = "Lister les réservations d'un membre")
    @GetMapping("/member/{memberId}")
    public List<Reservation> getByMember(@PathVariable Long memberId) {
        return reservationService.getByMember(memberId);
    }

    @Operation(summary = "Lister les réservations d'une salle")
    @GetMapping("/room/{roomId}")
    public List<Reservation> getByRoom(@PathVariable Long roomId) {
        return reservationService.getByRoom(roomId);
    }

    @Operation(summary = "Créer une réservation")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Réservation confirmée"),
        @ApiResponse(responseCode = "400", description = "Salle indisponible ou membre suspendu"),
        @ApiResponse(responseCode = "404", description = "Salle ou membre introuvable")
    })
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }

    @Operation(summary = "Annuler une réservation")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Réservation annulée"),
        @ApiResponse(responseCode = "400", description = "La réservation n'est pas confirmée"),
        @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @Operation(summary = "Marquer une réservation comme terminée")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Réservation complétée"),
        @ApiResponse(responseCode = "400", description = "La réservation n'est pas confirmée"),
        @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Reservation> completeReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.completeReservation(id));
    }
}
