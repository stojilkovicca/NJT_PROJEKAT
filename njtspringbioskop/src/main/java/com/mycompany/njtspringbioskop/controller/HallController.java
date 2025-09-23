/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.impl.HallDto;
import com.mycompany.njtspringbioskop.servis.HallServis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/api/hall")
public class HallController {

    private final HallServis hallServis;

    public HallController(HallServis hallServis) {
        this.hallServis = hallServis;
    }

    @GetMapping
    @Operation(summary = "Retrieve all Hall entities.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = HallDto.class), mediaType = "application/json")
    })
    public ResponseEntity<List<HallDto>> getAll() {
        return new ResponseEntity<>(hallServis.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HallDto> getById(
            @NotNull(message = "Should not be null")
            @PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(hallServis.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HallController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Hall entity.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = HallDto.class), mediaType = "application/json")
    })
    public ResponseEntity<HallDto> addHall(@Valid @RequestBody @NotNull HallDto hallDto) {
        try {
            HallDto saved = hallServis.create(hallDto);
            return ResponseEntity.created(URI.create("/api/hall/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving hall: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Hall entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = HallDto.class), mediaType = "application/json")
    })
    public ResponseEntity<HallDto> updateHall(
            @PathVariable Long id,
            @Valid @RequestBody HallDto hallDto) {
        try {
            hallDto.setId(id);
            HallDto updated = hallServis.update(hallDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating hall: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            hallServis.deleteById(id);
            return new ResponseEntity<>("Hall successfully deleted.", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Hall does not exist: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // ---- GENERISANJE SEDIŠTA ----
    @PostMapping("/{id}/generate-seats")
    @Operation(summary = "Generate seats grid for a hall (rows x seatsPerRow).")
    public ResponseEntity<HallDto> generateSeats(
            @PathVariable("id") Long hallId,
            @RequestParam("rows") @Min(1) int rows,
            @RequestParam("seatsPerRow") @Min(1) int seatsPerRow) {
        try {
            HallDto updated = hallServis.generateSeats(hallId, rows, seatsPerRow);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while generating seats: " + ex.getMessage());
        }
    }
}