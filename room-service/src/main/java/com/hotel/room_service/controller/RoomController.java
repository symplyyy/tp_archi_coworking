package com.hotel.room_service.controller;

import com.hotel.room_service.model.Room;
import com.hotel.room_service.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@Tag(name = "Salles", description = "Gestion des salles de coworking")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Lister toutes les salles")
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @Operation(summary = "Lister les salles disponibles")
    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    @Operation(summary = "Lister les salles par ville")
    @GetMapping("/city/{city}")
    public List<Room> getRoomsByCity(@PathVariable String city) {
        return roomService.getRoomsByCity(city);
    }

    @Operation(summary = "Récupérer une salle par son id")
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @Operation(summary = "Créer une salle")
    @ApiResponse(responseCode = "201", description = "Salle créée")
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(room));
    }

    @Operation(summary = "Modifier une salle")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Salle mise à jour"),
        @ApiResponse(responseCode = "404", description = "Salle introuvable")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @Operation(summary = "Supprimer une salle")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Salle supprimée"),
        @ApiResponse(responseCode = "404", description = "Salle introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Room> updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return ResponseEntity.ok(roomService.updateAvailability(id, available));
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Room> updateAvailabilityPut(@PathVariable Long id, @RequestParam boolean available) {
        return ResponseEntity.ok(roomService.updateAvailability(id, available));
    }
}
