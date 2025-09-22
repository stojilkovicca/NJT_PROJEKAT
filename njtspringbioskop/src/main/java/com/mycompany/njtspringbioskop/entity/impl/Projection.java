/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.entity.impl;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projections",
       indexes = {
           @Index(name = "idx_projection_datetime", columnList = "date_time")
       })
public class Projection {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private double basePrice;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @OneToMany(mappedBy = "projection", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "projection", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Reservation> reservations = new ArrayList<>();

    public Projection() {}
    public Projection(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public Hall getHall() { return hall; }
    public void setHall(Hall hall) { this.hall = hall; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
}