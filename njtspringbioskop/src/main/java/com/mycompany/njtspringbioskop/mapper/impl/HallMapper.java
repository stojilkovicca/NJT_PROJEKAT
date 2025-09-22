/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.mapper.impl;

 
import com.mycompany.njtspringbioskop.dto.impl.HallDto;
import com.mycompany.njtspringbioskop.entity.impl.Hall;
import com.mycompany.njtspringbioskop.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class HallMapper implements DtoEntityMapper<HallDto, Hall> {

    @Override
    public HallDto toDto(Hall e) {
        if (e == null) return null;
        return new HallDto(
            e.getId(),
            e.getName(),
            e.getCapacity(),
            e.getRows(),
            e.getSeatsPerRow()
        );
    }

    @Override
    public Hall toEntity(HallDto t) {
        if (t == null) return null;
        Hall h = new Hall();
        h.setId(t.getId());
        h.setName(t.getName());
        h.setCapacity(t.getCapacity());
        h.setRows(t.getRows());
        h.setSeatsPerRow(t.getSeatsPerRow());
        return h;
    }
}