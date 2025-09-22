/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.mapper.impl;

import com.mycompany.njtspringbioskop.dto.impl.GenreDto;
import com.mycompany.njtspringbioskop.entity.impl.Genre;
import com.mycompany.njtspringbioskop.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper implements DtoEntityMapper<GenreDto, Genre> {

    @Override
    public GenreDto toDto(Genre e) {
        if (e == null) return null;
        return new GenreDto(e.getId(), e.getName());
    }

    @Override
    public Genre toEntity(GenreDto t) {
        if (t == null) return null;
        Genre g = new Genre();
        g.setId(t.getId());
        g.setName(t.getName());
        return g;
    }
}