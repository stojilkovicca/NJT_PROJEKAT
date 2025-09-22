/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.entity.impl;
import jakarta.persistence.*;

@Entity
@Table(name = "tickets",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_ticket_projection_seat", columnNames = {"projection_id", "seat_id"}),
           @UniqueConstraint(name = "uk_ticket_qrcode", columnNames = {"qr_code"})
       })
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double ticketPrice;

    @Column(name = "qr_code", length = 120)
    private String qrCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "projection_id", nullable = false)
    private Projection projection;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    public Ticket() {}
    public Ticket(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public Projection getProjection() { return projection; }
    public void setProjection(Projection projection) { this.projection = projection; }
    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}
