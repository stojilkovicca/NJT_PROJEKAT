/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.mapper.impl;
 
import com.mycompany.njtspringbioskop.dto.impl.SeatDto;
import com.mycompany.njtspringbioskop.entity.impl.Hall;
import com.mycompany.njtspringbioskop.entity.impl.Seat;
import com.mycompany.njtspringbioskop.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper implements DtoEntityMapper<SeatDto, Seat> {

    @Override
    public SeatDto toDto(Seat e) {
        if (e == null) return null;
        return new SeatDto(
            e.getId(),
            e.getHall() != null ? e.getHall().getId() : null,
            e.getRowNumber(),
            e.getSeatNumber(),
            e.getLabel()
        );
    }

    @Override
    public Seat toEntity(SeatDto t) {
        if (t == null) return null;
        Seat s = new Seat();
        s.setId(t.getId());
        if (t.getHallId() != null) s.setHall(new Hall(t.getHallId()));
        s.setRowNumber(t.getRowNumber());
        s.setSeatNumber(t.getSeatNumber());
        s.setLabel(t.getLabel());
        return s;
    }
}