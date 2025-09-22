/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.mapper.impl;

import com.mycompany.njtspringbioskop.dto.impl.ProjectionDto; 
import com.mycompany.njtspringbioskop.entity.impl.Hall;
import com.mycompany.njtspringbioskop.entity.impl.Movie;
import com.mycompany.njtspringbioskop.entity.impl.Projection;
import com.mycompany.njtspringbioskop.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectionMapper implements DtoEntityMapper<ProjectionDto, Projection> {

    @Override
    public ProjectionDto toDto(Projection e) {
        if (e == null) return null;
        return new ProjectionDto(
            e.getId(),
            e.getDateTime(),
            e.getBasePrice(),
            e.getHall()  != null ? e.getHall().getId()  : null,
            e.getMovie() != null ? e.getMovie().getId() : null
        );
    }

    @Override
    public Projection toEntity(ProjectionDto t) {
        if (t == null) return null;
        Projection p = new Projection();
        p.setId(t.getId());
        p.setDateTime(t.getDateTime());
        p.setBasePrice(t.getBasePrice());
        if (t.getHallId()  != null) p.setHall(new Hall(t.getHallId()));
        if (t.getMovieId() != null) p.setMovie(new Movie(t.getMovieId()));
        return p;
    }
}