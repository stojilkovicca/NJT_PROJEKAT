/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.impl.GenreDto;
import com.mycompany.njtspringbioskop.servis.GenreServis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "http://localhost:4200")  
@RestController
@RequestMapping("/api/genre")
public class GenreController {

    private final GenreServis genreServis;

    public GenreController(GenreServis genreServis) {
        this.genreServis = genreServis;
    }

    @GetMapping
    @Operation(summary = "Retrieve all Genre entities.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = GenreDto.class), mediaType = "application/json")
    })
    public ResponseEntity<List<GenreDto>> getAll() {
        return new ResponseEntity<>(genreServis.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getById(
            @NotNull(message = "Should not be null or empty.")
            @PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(genreServis.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GenreController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Genre entity.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = GenreDto.class), mediaType = "application/json")
    })
    public ResponseEntity<GenreDto> addGenre(@Valid @RequestBody @NotNull GenreDto genreDto) {
        try {
            GenreDto saved = genreServis.create(genreDto);
            return ResponseEntity.created(URI.create("/api/genre/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving genre: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Genre entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = GenreDto.class), mediaType = "application/json")
    })
    public ResponseEntity<GenreDto> updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody GenreDto genreDto) {
        try {
            genreDto.setId(id); // ažurira se pravi entitet
            GenreDto updated = genreServis.update(genreDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating genre: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            genreServis.deleteById(id);
            return new ResponseEntity<>("Genre successfully deleted.", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Genre does not exist: " + id, HttpStatus.NOT_FOUND);
        }
    }
}