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
@Table(name = "reservations")
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int numberOfTickets;

    @Column(nullable = false)
    private LocalDateTime reservedAt = LocalDateTime.now();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "projection_id", nullable = false)
    private Projection projection;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    public Reservation() {}
    public Reservation(Long id) { this.id = id; }

    // helpers
    public void addTicket(Ticket t) { t.setReservation(this); this.tickets.add(t); }
    public void removeTicket(Ticket t) { t.setReservation(null); this.tickets.remove(t); }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getNumberOfTickets() { return numberOfTickets; }
    public void setNumberOfTickets(int numberOfTickets) { this.numberOfTickets = numberOfTickets; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public void setReservedAt(LocalDateTime reservedAt) { this.reservedAt = reservedAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Projection getProjection() { return projection; }
    public void setProjection(Projection projection) { this.projection = projection; }
    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }
}