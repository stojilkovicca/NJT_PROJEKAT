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

public class HallDto implements MyDto {
    private Long id;

    @NotBlank(message = "name is required")
    @Size(max = 80, message = "name max 80 chars")
    private String name;

    @Min(value = 1, message = "capacity must be > 0")
    private int capacity;

    // opciono
    private Integer rows;
    private Integer seatsPerRow;

    public HallDto() {}
    public HallDto(Long id, String name, int capacity, Integer rows, Integer seatsPerRow) {
        this.id = id; this.name = name; this.capacity = capacity;
        this.rows = rows; this.seatsPerRow = seatsPerRow;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public int getCapacity() {return capacity;}
    public void setCapacity(int capacity) {this.capacity = capacity;}
    public Integer getRows() {return rows;}
    public void setRows(Integer rows) {this.rows = rows;}
    public Integer getSeatsPerRow() {return seatsPerRow;}
    public void setSeatsPerRow(Integer seatsPerRow) {this.seatsPerRow = seatsPerRow;}
}