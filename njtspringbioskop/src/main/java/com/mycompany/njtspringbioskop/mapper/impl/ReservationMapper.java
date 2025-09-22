/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.mapper.impl;

import com.mycompany.njtspringbioskop.dto.impl.ReservationDto;
import com.mycompany.njtspringbioskop.dto.impl.TicketDto;
import com.mycompany.njtspringbioskop.entity.impl.Projection;
import com.mycompany.njtspringbioskop.entity.impl.Reservation;
import com.mycompany.njtspringbioskop.entity.impl.User;
import com.mycompany.njtspringbioskop.mapper.DtoEntityMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper implements DtoEntityMapper<ReservationDto, Reservation> {

    private final TicketMapper ticketMapper;

    public ReservationMapper(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    @Override
    public ReservationDto toDto(Reservation e) {
        if (e == null) return null;

        List<TicketDto> ticketDtos = null;
        if (e.getTickets() != null) {
            ticketDtos = e.getTickets().stream()
                    .map(ticketMapper::toDto)
                    .collect(Collectors.toList());
        }

        return new ReservationDto(
            e.getId(),
            e.getNumberOfTickets(),
            e.getReservedAt(),
            e.getUser()       != null ? e.getUser().getId()       : null,
            e.getProjection() != null ? e.getProjection().getId() : null,
            ticketDtos
        );
    }

    @Override
    public Reservation toEntity(ReservationDto t) {
        if (t == null) return null;

        Reservation r = new Reservation();
        r.setId(t.getId());
        r.setNumberOfTickets(t.getNumberOfTickets());
        if (t.getUserId() != null)       r.setUser(new User(t.getUserId()));
        if (t.getProjectionId() != null) r.setProjection(new Projection(t.getProjectionId()));

        // Ako DTO sadrži karte, mapiraj ih i poveži na rezervaciju
        if (t.getTickets() != null) {
            t.getTickets().forEach(td -> {
                var ticket = ticketMapper.toEntity(td);
                // osiguraj referencu na ovu rezervaciju
                ticket.setReservation(r);
                r.getTickets().add(ticket);
            });
        }
        return r;
    }
}