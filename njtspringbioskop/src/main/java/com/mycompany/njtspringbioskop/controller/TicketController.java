package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.impl.TicketDto;
import com.mycompany.njtspringbioskop.servis.TicketServis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketServis ticketServis;

    public TicketController(TicketServis ticketServis) {
        this.ticketServis = ticketServis;
    }

    // ——— CRUD ———

    @GetMapping
    @Operation(summary = "Retrieve all Ticket entities.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = TicketDto.class), mediaType = "application/json")
    })
    public ResponseEntity<List<TicketDto>> getAll() {
        return new ResponseEntity<>(ticketServis.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(ticketServis.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TicketController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Ticket entity.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = TicketDto.class), mediaType = "application/json")
    })
    public ResponseEntity<TicketDto> add(@Valid @RequestBody @NotNull TicketDto dto) {
        try {
            TicketDto saved = ticketServis.create(dto);
            return ResponseEntity.created(URI.create("/api/ticket/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving ticket: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Ticket entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = TicketDto.class), mediaType = "application/json")
    })
    public ResponseEntity<TicketDto> update(@PathVariable Long id, @Valid @RequestBody TicketDto dto) {
        try {
            TicketDto updated = ticketServis.update(id, dto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating ticket: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            ticketServis.deleteById(id);
            return new ResponseEntity<>("Ticket successfully deleted.", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Ticket does not exist: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // ——— Filteri ———

    @GetMapping("/projection/{projectionId}")
    public ResponseEntity<List<TicketDto>> byProjection(@PathVariable Long projectionId) {
        return new ResponseEntity<>(ticketServis.findByProjection(projectionId), HttpStatus.OK);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<TicketDto>> byReservation(@PathVariable Long reservationId) {
        return new ResponseEntity<>(ticketServis.findByReservation(reservationId), HttpStatus.OK);
    }

    @GetMapping("/seat/{seatId}")
    public ResponseEntity<List<TicketDto>> bySeat(@PathVariable Long seatId) {
        return new ResponseEntity<>(ticketServis.findBySeat(seatId), HttpStatus.OK);
    }

    @GetMapping("/qr/{qr}")
    public ResponseEntity<TicketDto> byQr(@PathVariable String qr) {
        try {
            return new ResponseEntity<>(ticketServis.findByQrCode(qr), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
