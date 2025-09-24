package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.impl.ProjectionDto;
import com.mycompany.njtspringbioskop.servis.ProjectionServis;
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
@RequestMapping("/api/projection")
public class ProjectionController {

    private final ProjectionServis projectionServis;

    public ProjectionController(ProjectionServis projectionServis) {
        this.projectionServis = projectionServis;
    }

    @GetMapping
    @Operation(summary = "Retrieve all Projection entities.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = ProjectionDto.class), mediaType = "application/json")
    })
    public ResponseEntity<List<ProjectionDto>> getAll() {
        return new ResponseEntity<>(projectionServis.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectionDto> getById(
            @NotNull @PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(projectionServis.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProjectionController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Projection entity.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = ProjectionDto.class), mediaType = "application/json")
    })
    public ResponseEntity<ProjectionDto> add(@Valid @RequestBody @NotNull ProjectionDto dto) {
        try {
            ProjectionDto saved = projectionServis.create(dto);
            return ResponseEntity.created(URI.create("/api/projection/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while saving projection: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Projection entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = ProjectionDto.class), mediaType = "application/json")
    })
    public ResponseEntity<ProjectionDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectionDto dto) {
        try {
            ProjectionDto updated = projectionServis.update(id, dto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating projection: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            projectionServis.deleteById(id);
            return new ResponseEntity<>("Projection successfully deleted.", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Projection does not exist: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // ——— Jednostavni filteri ———

    @GetMapping("/hall/{hallId}")
    public ResponseEntity<List<ProjectionDto>> byHall(@PathVariable Long hallId) {
        return new ResponseEntity<>(projectionServis.findByHall(hallId), HttpStatus.OK);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ProjectionDto>> byMovie(@PathVariable Long movieId) {
        return new ResponseEntity<>(projectionServis.findByMovie(movieId), HttpStatus.OK);
    }

    @GetMapping("/range")
    public ResponseEntity<List<ProjectionDto>> byRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return new ResponseEntity<>(projectionServis.findInRange(from, to), HttpStatus.OK);
    }
}
