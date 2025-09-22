/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.entity.impl;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls",
       uniqueConstraints = @UniqueConstraint(name = "uk_hall_name", columnNames = "name"))
public class Hall {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private int capacity;

    // opciono: ako želiš i ovde držati raspored
    private Integer rows;
    private Integer seatsPerRow;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Projection> projections = new ArrayList<>();

    public Hall() {}
    public Hall(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public Integer getRows() { return rows; }
    public void setRows(Integer rows) { this.rows = rows; }
    public Integer getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(Integer seatsPerRow) { this.seatsPerRow = seatsPerRow; }
    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
    public List<Projection> getProjections() { return projections; }
    public void setProjections(List<Projection> projections) { this.projections = projections; }
}
