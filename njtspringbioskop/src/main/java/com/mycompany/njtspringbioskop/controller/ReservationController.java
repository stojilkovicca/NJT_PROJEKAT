package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.impl.ReservationDto;
import com.mycompany.njtspringbioskop.servis.ReservationServis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationServis reservationServis;

    public ReservationController(ReservationServis reservationServis) {
        this.reservationServis = reservationServis;
    }

    // ——— CRUD ———

    @GetMapping
    @Operation(summary = "Retrieve all Reservation entities.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = ReservationDto.class), mediaType = "application/json")
    })
    public ResponseEntity<List<ReservationDto>> getAll() {
        return new ResponseEntity<>(reservationServis.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(reservationServis.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ReservationController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Reservation entity.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = ReservationDto.class), mediaType = "application/json")
    })
    public ResponseEntity<ReservationDto> add(@Valid @RequestBody @NotNull ReservationDto dto) {
        try {
            ReservationDto saved = reservationServis.create(dto);
            return ResponseEntity.created(URI.create("/api/reservation/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving reservation: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Reservation entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = ReservationDto.class), mediaType = "application/json")
    })
    public ResponseEntity<ReservationDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDto dto) {
        try {
            ReservationDto updated = reservationServis.update(id, dto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating reservation: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            reservationServis.deleteById(id);
            return new ResponseEntity<>("Reservation successfully deleted.", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Reservation does not exist: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // ——— Filteri ———

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDto>> byUser(@PathVariable Long userId) {
        return new ResponseEntity<>(reservationServis.findByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/projection/{projectionId}")
    public ResponseEntity<List<ReservationDto>> byProjection(@PathVariable Long projectionId) {
        return new ResponseEntity<>(reservationServis.findByProjection(projectionId), HttpStatus.OK);
    }

    @GetMapping("/range")
    public ResponseEntity<List<ReservationDto>> byRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return new ResponseEntity<>(reservationServis.findInRange(from, to), HttpStatus.OK);
    }

    @GetMapping("/projection/{projectionId}/sum-tickets")
    public ResponseEntity<Long> sumTickets(@PathVariable Long projectionId) {
        return new ResponseEntity<>(reservationServis.reservedTicketsForProjection(projectionId), HttpStatus.OK);
    }
}
