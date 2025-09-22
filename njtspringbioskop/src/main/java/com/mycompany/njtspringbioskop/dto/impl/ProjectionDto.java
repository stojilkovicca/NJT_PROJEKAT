/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.dto.impl;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import com.mycompany.njtspringbioskop.dto.MyDto;

public class ProjectionDto implements MyDto {
    private Long id;

    @NotNull(message = "dateTime is required")
    private LocalDateTime dateTime;

    @Min(value = 0, message = "basePrice must be >= 0")
    private double basePrice;

    @NotNull(message = "hallId is required")
    private Long hallId;

    @NotNull(message = "movieId is required")
    private Long movieId;

    public ProjectionDto() {}
    public ProjectionDto(Long id, LocalDateTime dateTime, double basePrice, Long hallId, Long movieId) {
        this.id = id; this.dateTime = dateTime; this.basePrice = basePrice; this.hallId = hallId; this.movieId = movieId;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public LocalDateTime getDateTime() {return dateTime;}
    public void setDateTime(LocalDateTime dateTime) {this.dateTime = dateTime;}
    public double getBasePrice() {return basePrice;}
    public void setBasePrice(double basePrice) {this.basePrice = basePrice;}
    public Long getHallId() {return hallId;}
    public void setHallId(Long hallId) {this.hallId = hallId;}
    public Long getMovieId() {return movieId;}
    public void setMovieId(Long movieId) {this.movieId = movieId;}
}