/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.impl.MovieDto;
import com.mycompany.njtspringbioskop.servis.MovieServis;
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
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieServis movieServis;

    public MovieController(MovieServis movieServis) {
        this.movieServis = movieServis;
    }

    @GetMapping
    @Operation(summary = "Retrieve all Movie entities.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = MovieDto.class), mediaType = "application/json")
    })
    public ResponseEntity<List<MovieDto>> getAll() {
        return new ResponseEntity<>(movieServis.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getById(
            @NotNull(message = "Should not be null or empty.")
            @PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(movieServis.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MovieController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Movie entity.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = MovieDto.class), mediaType = "application/json")
    })
    public ResponseEntity<MovieDto> addMovie(@Valid @RequestBody @NotNull MovieDto movieDto) {
        try {
            MovieDto saved = movieServis.create(movieDto);
            return ResponseEntity.created(URI.create("/api/movie/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving movie: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Movie entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = MovieDto.class), mediaType = "application/json")
    })
    public ResponseEntity<MovieDto> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieDto movieDto) {
        try {
            movieDto.setId(id);
            MovieDto updated = movieServis.update(movieDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating movie: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            movieServis.deleteById(id);
            return new ResponseEntity<>("Movie successfully deleted.", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Movie does not exist: " + id, HttpStatus.NOT_FOUND);
        }
    }
}