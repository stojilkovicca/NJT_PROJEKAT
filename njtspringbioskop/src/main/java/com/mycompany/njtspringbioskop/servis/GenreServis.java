/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.servis;
 

import com.mycompany.njtspringbioskop.dto.impl.GenreDto;
import com.mycompany.njtspringbioskop.entity.impl.Genre;
import com.mycompany.njtspringbioskop.mapper.impl.GenreMapper;
import com.mycompany.njtspringbioskop.repository.impl.GenreRepository;
import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenreServis {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Autowired
    public GenreServis(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    public List<GenreDto> findAll() {
        return genreRepository.findAll()
                .stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    public GenreDto findById(Long id) throws Exception {
        return genreMapper.toDto(genreRepository.findById(id));
    }

    public GenreDto create(GenreDto dto) {
        // opciono: provera duplikata po nazivu
        if (dto.getName() != null && genreRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new RuntimeException("Žanr sa datim nazivom već postoji: " + dto.getName());
        }
        Genre g = genreMapper.toEntity(dto);
        genreRepository.save(g);
        return genreMapper.toDto(g);
    }

    public GenreDto update(GenreDto dto) {
        // isti stil kao kod RestaurantServis: direktno merge preko save()
        Genre updated = genreMapper.toEntity(dto);
        genreRepository.save(updated);
        return genreMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        genreRepository.deleteById(id);
    }
}
