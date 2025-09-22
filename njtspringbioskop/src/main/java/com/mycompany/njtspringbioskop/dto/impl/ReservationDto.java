/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.dto.impl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import com.mycompany.njtspringbioskop.dto.MyDto;

public class ReservationDto implements MyDto {
    private Long id;

    @Min(value = 1, message = "numberOfTickets must be >= 1")
    private int numberOfTickets;

    private LocalDateTime reservedAt; // read-only (postavlja entitet)

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "projectionId is required")
    private Long projectionId;

    // Ako želiš da kreiraš sa kartama u jednom koraku:
    @Valid
    private List<TicketDto> tickets;

    public ReservationDto() {}

    public ReservationDto(Long id, int numberOfTickets, LocalDateTime reservedAt,
                          Long userId, Long projectionId, List<TicketDto> tickets) {
        this.id = id; this.numberOfTickets = numberOfTickets; this.reservedAt = reservedAt;
        this.userId = userId; this.projectionId = projectionId; this.tickets = tickets;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public int getNumberOfTickets() {return numberOfTickets;}
    public void setNumberOfTickets(int numberOfTickets) {this.numberOfTickets = numberOfTickets;}
    public LocalDateTime getReservedAt() {return reservedAt;}
    public void setReservedAt(LocalDateTime reservedAt) {this.reservedAt = reservedAt;}
    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}
    public Long getProjectionId() {return projectionId;}
    public void setProjectionId(Long projectionId) {this.projectionId = projectionId;}
    public List<TicketDto> getTickets() {return tickets;}
    public void setTickets(List<TicketDto> tickets) {this.tickets = tickets;}
}
