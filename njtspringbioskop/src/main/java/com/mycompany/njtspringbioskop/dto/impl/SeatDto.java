/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.dto.impl;
 
import com.mycompany.njtspringbioskop.dto.MyDto; 
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SeatDto implements MyDto {
    private Long id;

    @NotNull(message = "hallId is required")
    private Long hallId;

    @Min(value = 1, message = "rowNumber must be >= 1")
    private int rowNumber;

    @Min(value = 1, message = "seatNumber must be >= 1")
    private int seatNumber;

    @Size(max = 20, message = "label max 20 chars")
    private String label; // npr. B-12

    public SeatDto() {}
    public SeatDto(Long id, Long hallId, int rowNumber, int seatNumber, String label) {
        this.id = id; this.hallId = hallId; this.rowNumber = rowNumber; this.seatNumber = seatNumber; this.label = label;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getHallId() {return hallId;}
    public void setHallId(Long hallId) {this.hallId = hallId;}
    public int getRowNumber() {return rowNumber;}
    public void setRowNumber(int rowNumber) {this.rowNumber = rowNumber;}
    public int getSeatNumber() {return seatNumber;}
    public void setSeatNumber(int seatNumber) {this.seatNumber = seatNumber;}
    public String getLabel() {return label;}
    public void setLabel(String label) {this.label = label;}
}