/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.servis;

import com.mycompany.njtspringbioskop.dto.impl.HallDto;
import com.mycompany.njtspringbioskop.entity.impl.Hall;
import com.mycompany.njtspringbioskop.entity.impl.Seat;
import com.mycompany.njtspringbioskop.mapper.impl.HallMapper;
import com.mycompany.njtspringbioskop.repository.impl.HallRepository;
import java.util.List;
import java.util.stream.Collectors;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HallServis {

    private final HallRepository hallRepository;
    private final HallMapper hallMapper;

    @Autowired
    public HallServis(HallRepository hallRepository, HallMapper hallMapper) {
        this.hallRepository = hallRepository;
        this.hallMapper = hallMapper;
    }

    public List<HallDto> findAll() {
        return hallRepository.findAll()
                .stream()
                .map(hallMapper::toDto)
                .collect(Collectors.toList());
    }

    public HallDto findById(Long id) throws Exception {
        return hallMapper.toDto(hallRepository.findById(id));
    }

    public HallDto create(HallDto dto) {
        Hall h = hallMapper.toEntity(dto);
        hallRepository.save(h);
        return hallMapper.toDto(h);
    }

    public HallDto update(HallDto dto) {
        Hall updated = hallMapper.toEntity(dto);
        hallRepository.save(updated);
        return hallMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        hallRepository.deleteById(id);
    }

    /**
     * Generise mrezu sedista za salu (rows x seatsPerRow).
     * Takodje azurira capacity = rows * seatsPerRow  
     */
    public HallDto generateSeats(Long hallId, int rows, int seatsPerRow) throws Exception {
        if (rows <= 0 || seatsPerRow <= 0) {
            throw new IllegalArgumentException("rows i seatsPerRow moraju biti > 0");
        }

        Hall hall = hallRepository.findById(hallId);
        hall.setRows(rows);
        hall.setSeatsPerRow(seatsPerRow);
        hall.setCapacity(rows * seatsPerRow);
        hallRepository.save(hall); // merge atributa sale

        // Kreiraj (ako ne postoje) sva sedišta po mreži
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= seatsPerRow; c++) {
                if (!hallRepository.seatExists(hall.getId(), r, c)) {
                    Seat s = new Seat();
                    s.setHall(hall);
                    s.setRowNumber(r);
                    s.setSeatNumber(c);
                    s.setLabel("R" + r + "-S" + c);
                    hallRepository.saveSeat(s);
                }
            }
        }

        return hallMapper.toDto(hall);
    }
}