/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.entity.impl;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int duration;          // u minutima

    private Double rating;         // interna ocena
    private Double imdbRating;     // opcionalno

    @Column(length = 120)
    private String producer;

    @Column(length = 500)
    private String actors;         // jednostavno: csv; za naprednije -> posebna tabela

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Projection> projections = new ArrayList<>();

    public Movie() {}
    public Movie(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Double getImdbRating() { return imdbRating; }
    public void setImdbRating(Double imdbRating) { this.imdbRating = imdbRating; }
    public String getProducer() { return producer; }
    public void setProducer(String producer) { this.producer = producer; }
    public String getActors() { return actors; }
    public void setActors(String actors) { this.actors = actors; }
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public List<Projection> getProjections() { return projections; }
    public void setProjections(List<Projection> projections) { this.projections = projections; }
}