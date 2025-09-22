/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.mapper.impl;

import com.mycompany.njtspringbioskop.dto.impl.MovieDto;
import com.mycompany.njtspringbioskop.entity.impl.Genre;
import com.mycompany.njtspringbioskop.entity.impl.Movie;
import com.mycompany.njtspringbioskop.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper implements DtoEntityMapper<MovieDto, Movie> {

    @Override
    public MovieDto toDto(Movie e) {
        if (e == null) return null;
        return new MovieDto(
            e.getId(),
            e.getTitle(),
            e.getDescription(),
            e.getDuration(),
            e.getRating(),
            e.getImdbRating(),
            e.getProducer(),
            e.getActors(),
            e.getGenre() != null ? e.getGenre().getId() : null
        );
    }

    @Override
    public Movie toEntity(MovieDto t) {
        if (t == null) return null;
        Movie m = new Movie();
        m.setId(t.getId());
        m.setTitle(t.getTitle());
        m.setDescription(t.getDescription());
        m.setDuration(t.getDuration());
        m.setRating(t.getRating());
        m.setImdbRating(t.getImdbRating());
        m.setProducer(t.getProducer());
        m.setActors(t.getActors());
        if (t.getGenreId() != null) m.setGenre(new Genre(t.getGenreId()));
        return m;
    }
}