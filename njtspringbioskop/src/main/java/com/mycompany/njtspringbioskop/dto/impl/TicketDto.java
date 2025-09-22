/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.dto.impl;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.mycompany.njtspringbioskop.dto.MyDto;

public class TicketDto implements MyDto {
    private Long id;

    @Min(value = 0, message = "ticketPrice must be >= 0")
    private double ticketPrice;

    @Size(max = 120, message = "qrCode max 120 chars")
    private String qrCode;

    @NotNull(message = "projectionId is required")
    private Long projectionId;

    @NotNull(message = "seatId is required")
    private Long seatId;

    @NotNull(message = "reservationId is required")
    private Long reservationId;

    public TicketDto() {}
    public TicketDto(Long id, double ticketPrice, String qrCode, Long projectionId, Long seatId, Long reservationId) {
        this.id = id; this.ticketPrice = ticketPrice; this.qrCode = qrCode;
        this.projectionId = projectionId; this.seatId = seatId; this.reservationId = reservationId;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public double getTicketPrice() {return ticketPrice;}
    public void setTicketPrice(double ticketPrice) {this.ticketPrice = ticketPrice;}
    public String getQrCode() {return qrCode;}
    public void setQrCode(String qrCode) {this.qrCode = qrCode;}
    public Long getProjectionId() {return projectionId;}
    public void setProjectionId(Long projectionId) {this.projectionId = projectionId;}
    public Long getSeatId() {return seatId;}
    public void setSeatId(Long seatId) {this.seatId = seatId;}
    public Long getReservationId() {return reservationId;}
    public void setReservationId(Long reservationId) {this.reservationId = reservationId;}
}