package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.impl.SeatDto;
import com.mycompany.njtspringbioskop.servis.SeatServis;
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
@RequestMapping("/api/seat")
public class SeatController {

    private final SeatServis seatServis;

    public SeatController(SeatServis seatServis) {
        this.seatServis = seatServis;
    }

    // ——— CRUD ———

    @GetMapping
    @Operation(summary = "Retrieve all Seat entities.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = SeatDto.class), mediaType = "application/json")
    })
    public ResponseEntity<List<SeatDto>> getAll() {
        return new ResponseEntity<>(seatServis.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatDto> getById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(seatServis.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SeatController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Seat entity.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = SeatDto.class), mediaType = "application/json")
    })
    public ResponseEntity<SeatDto> add(@Valid @RequestBody @NotNull SeatDto dto) {
        try {
            SeatDto saved = seatServis.create(dto);
            return ResponseEntity.created(URI.create("/api/seat/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving seat: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Seat entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = SeatDto.class), mediaType = "application/json")
    })
    public ResponseEntity<SeatDto> update(@PathVariable Long id, @Valid @RequestBody SeatDto dto) {
        try {
            SeatDto updated = seatServis.update(id, dto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating seat: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            seatServis.deleteById(id);
            return new ResponseEntity<>("Seat successfully deleted.", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Seat does not exist: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // ——— Filteri ———

    @GetMapping("/hall/{hallId}")
    public ResponseEntity<List<SeatDto>> byHall(@PathVariable Long hallId) {
        return new ResponseEntity<>(seatServis.findByHall(hallId), HttpStatus.OK);
    }

 

    // ——— Bulk generate (opciono) ———
    @PostMapping("/hall/{hallId}/generate")
    @Operation(summary = "Generate a grid of seats for a hall.")
    public ResponseEntity<List<SeatDto>> generate(
            @PathVariable Long hallId,
            @RequestParam int rows,
            @RequestParam int seatsPerRow,
            @RequestParam(defaultValue = "true") boolean overwriteLabels) {
        try {
            List<SeatDto> created = seatServis.generateGrid(hallId, rows, seatsPerRow, overwriteLabels);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while generating seats: " + ex.getMessage());
        }
    }
    
    @DeleteMapping("/hall/{hallId}")
    public ResponseEntity<String> clearHall(@PathVariable Long hallId) {
        seatServis.deleteByHall(hallId);
        return ResponseEntity.ok("Seats cleared for hall " + hallId);
    }
}
