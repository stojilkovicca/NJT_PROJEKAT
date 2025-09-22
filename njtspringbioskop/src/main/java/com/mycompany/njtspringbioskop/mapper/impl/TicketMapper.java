/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.mapper.impl;

import com.mycompany.njtspringbioskop.dto.impl.TicketDto;
 
import com.mycompany.njtspringbioskop.entity.impl.Projection;
import com.mycompany.njtspringbioskop.entity.impl.Reservation;
import com.mycompany.njtspringbioskop.entity.impl.Seat;
import com.mycompany.njtspringbioskop.entity.impl.Ticket;
import com.mycompany.njtspringbioskop.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper implements DtoEntityMapper<TicketDto, Ticket> {

    @Override
    public TicketDto toDto(Ticket e) {
        if (e == null) return null;
        return new TicketDto(
            e.getId(),
            e.getTicketPrice(),
            e.getQrCode(),
            e.getProjection()  != null ? e.getProjection().getId()  : null,
            e.getSeat()        != null ? e.getSeat().getId()        : null,
            e.getReservation() != null ? e.getReservation().getId() : null
        );
    }

    @Override
    public Ticket toEntity(TicketDto t) {
        if (t == null) return null;
        Ticket tk = new Ticket();
        tk.setId(t.getId());
        tk.setTicketPrice(t.getTicketPrice());
        tk.setQrCode(t.getQrCode());
        if (t.getProjectionId()  != null) tk.setProjection(new Projection(t.getProjectionId()));
        if (t.getSeatId()        != null) tk.setSeat(new Seat(t.getSeatId()));
        if (t.getReservationId() != null) tk.setReservation(new Reservation(t.getReservationId()));
        return tk;
    }
}
