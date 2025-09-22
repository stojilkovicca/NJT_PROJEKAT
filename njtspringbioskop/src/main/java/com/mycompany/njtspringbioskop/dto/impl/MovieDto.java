/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.dto.impl;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.mycompany.njtspringbioskop.dto.MyDto;

public class MovieDto implements MyDto {
    private Long id;

    @NotBlank(message = "title is required")
    @Size(max = 160, message = "title max 160 chars")
    private String title;

    private String description;   // TEXT u entitetu

    @Min(value = 1, message = "duration must be > 0")
    private int duration;         // u minutima

    private Double rating;        // opcionalno
    private Double imdbRating;    // opcionalno

    @Size(max = 120, message = "producer max 120 chars")
    private String producer;

    @Size(max = 500, message = "actors max 500 chars")
    private String actors;        // csv ili tekst

    @NotNull(message = "genreId is required")
    private Long genreId;

    public MovieDto() {}

    public MovieDto(Long id, String title, String description, int duration,
                    Double rating, Double imdbRating, String producer, String actors, Long genreId) {
        this.id = id; this.title = title; this.description = description; this.duration = duration;
        this.rating = rating; this.imdbRating = imdbRating; this.producer = producer;
        this.actors = actors; this.genreId = genreId;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public int getDuration() {return duration;}
    public void setDuration(int duration) {this.duration = duration;}
    public Double getRating() {return rating;}
    public void setRating(Double rating) {this.rating = rating;}
    public Double getImdbRating() {return imdbRating;}
    public void setImdbRating(Double imdbRating) {this.imdbRating = imdbRating;}
    public String getProducer() {return producer;}
    public void setProducer(String producer) {this.producer = producer;}
    public String getActors() {return actors;}
    public void setActors(String actors) {this.actors = actors;}
    public Long getGenreId() {return genreId;}
    public void setGenreId(Long genreId) {this.genreId = genreId;}
}