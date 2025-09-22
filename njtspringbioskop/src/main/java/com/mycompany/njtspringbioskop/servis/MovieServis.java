/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.servis;
import com.mycompany.njtspringbioskop.dto.impl.MovieDto;
import com.mycompany.njtspringbioskop.entity.impl.Genre;
import com.mycompany.njtspringbioskop.entity.impl.Movie;
import com.mycompany.njtspringbioskop.mapper.impl.MovieMapper;
import com.mycompany.njtspringbioskop.repository.impl.MovieRepository;
import java.util.List;
import java.util.stream.Collectors; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class MovieServis {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public MovieServis(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    public List<MovieDto> findAll() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }

    public MovieDto findById(Long id) throws Exception {
        return movieMapper.toDto(movieRepository.findById(id));
    }

    public MovieDto create(MovieDto dto) {
        // opciona validacija duplikata po nazivu
        if (dto.getTitle() != null && movieRepository.existsByTitleIgnoreCase(dto.getTitle())) {
            throw new RuntimeException("Film sa tim naslovom već postoji: " + dto.getTitle());
        }
        // osiguraj referencu na žanr (bez SELECT-a) ako je postavljen genreId
        Movie m = movieMapper.toEntity(dto);
        if (dto.getGenreId() != null) {
            m.setGenre(em.getReference(Genre.class, dto.getGenreId()));
        }
        movieRepository.save(m);
        return movieMapper.toDto(m);
    }

    public MovieDto update(MovieDto dto) {
        // merge stil kao kod RestaurantServis
        Movie updated = movieMapper.toEntity(dto);
        if (dto.getGenreId() != null) {
            updated.setGenre(em.getReference(Genre.class, dto.getGenreId()));
        }
        movieRepository.save(updated);
        return movieMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }
}