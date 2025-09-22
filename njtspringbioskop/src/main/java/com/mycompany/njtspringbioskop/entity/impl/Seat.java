/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.entity.impl;
 

import jakarta.persistence.*;

@Entity
@Table(name = "seats",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_seat_hall_row_col",
           columnNames = {"hall_id", "row_number", "seat_number"}))
public class Seat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(length = 20)
    private String label; // npr. "B-12"

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    public Seat() {}
    public Seat(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getRowNumber() { return rowNumber; }
    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Hall getHall() { return hall; }
    public void setHall(Hall hall) { this.hall = hall; }
}
