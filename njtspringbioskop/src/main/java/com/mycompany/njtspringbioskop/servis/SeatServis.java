
package com.mycompany.njtspringbioskop.servis;

import com.mycompany.njtspringbioskop.dto.impl.SeatDto;
import com.mycompany.njtspringbioskop.entity.impl.Hall;
import com.mycompany.njtspringbioskop.entity.impl.Seat;
import com.mycompany.njtspringbioskop.mapper.impl.SeatMapper;
import com.mycompany.njtspringbioskop.repository.impl.SeatRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatServis {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    @PersistenceContext
    private EntityManager em;

    public SeatServis(SeatRepository seatRepository, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
    }

    // ——— CRUD ———

    public List<SeatDto> findAll() {
        return seatRepository.findAll().stream()
                .map(seatMapper::toDto)
                .collect(Collectors.toList());
    }

    public SeatDto findById(Long id) throws Exception {
        return seatMapper.toDto(seatRepository.findById(id));
    }

    public SeatDto create(SeatDto dto) throws Exception {
        validate(dto, false);

        // da li sala postoji?
        Hall hall = em.find(Hall.class, dto.getHallId());
        if (hall == null) throw new Exception("Sala ne postoji (hallId=" + dto.getHallId() + ").");

        // jedinstvenost u okviru sale (hall,row,seat)
        if (seatRepository.existsByHallRowSeat(dto.getHallId(), dto.getRowNumber(), dto.getSeatNumber())) {
            throw new Exception("Sedište već postoji u sali (hallId=" + dto.getHallId() +
                    ", row=" + dto.getRowNumber() + ", seat=" + dto.getSeatNumber() + ").");
        }

        Seat s = seatMapper.toEntity(dto);
        s.setHall(em.getReference(Hall.class, dto.getHallId()));
        seatRepository.save(s);
        return seatMapper.toDto(s);
    }

    public SeatDto update(Long id, SeatDto dto) throws Exception {
        if (id == null) throw new Exception("ID je obavezan.");
        dto.setId(id);
        validate(dto, true);

        // mora da postoji
        Seat existing = seatRepository.findById(id);

        // provera sale
        Hall hall = em.find(Hall.class, dto.getHallId());
        if (hall == null) throw new Exception("Sala ne postoji (hallId=" + dto.getHallId() + ").");

        // ako je promenjen (hall,row,seat) i time narušen unique
        boolean changingKey = !existing.getHall().getId().equals(dto.getHallId())
                || existing.getRowNumber() != dto.getRowNumber()
                || existing.getSeatNumber() != dto.getSeatNumber();

        if (changingKey && seatRepository.existsByHallRowSeat(dto.getHallId(), dto.getRowNumber(), dto.getSeatNumber())) {
            throw new Exception("Sedište već postoji u sali (hallId=" + dto.getHallId() +
                    ", row=" + dto.getRowNumber() + ", seat=" + dto.getSeatNumber() + ").");
        }

        Seat s = seatMapper.toEntity(dto);
        s.setHall(em.getReference(Hall.class, dto.getHallId()));
        seatRepository.save(s);
        return seatMapper.toDto(s);
    }

    public void deleteById(Long id) {
        seatRepository.deleteById(id);
    }

    // ——— Filteri ———

    public List<SeatDto> findByHall(Long hallId) {
        return seatRepository.findByHall(hallId).stream()
                .map(seatMapper::toDto).collect(Collectors.toList());
    }

    public List<SeatDto> findByHallAndRow(Long hallId, int rowNum) {
        return seatRepository.findByHallAndRow(hallId, rowNum).stream()
                .map(seatMapper::toDto).collect(Collectors.toList());
    }

    public List<SeatDto> searchByHallAndLabel(Long hallId, String q) {
        return seatRepository.searchByHallAndLabel(hallId, q).stream()
                .map(seatMapper::toDto).collect(Collectors.toList());
    }

    // ——— Bulk generate (opciono, korisno za inicijalno popunjavanje) ———
    @Transactional
    public List<SeatDto> generateGrid(Long hallId, int rows, int seatsPerRow, boolean overwriteLabels) throws Exception {
        if (hallId == null || rows < 1 || seatsPerRow < 1) {
            throw new Exception("Parametri nisu validni (hallId, rows, seatsPerRow).");
        }
        Hall h = em.find(Hall.class, hallId);
        if (h == null) throw new Exception("Sala ne postoji (hallId=" + hallId + ").");

        List<SeatDto> created = new ArrayList<>();
        for (int r = 1; r <= rows; r++) {
            for (int n = 1; n <= seatsPerRow; n++) {
                if (seatRepository.existsByHallRowSeat(hallId, r, n)) continue; // preskoči postojeće
                Seat s = new Seat();
                s.setHall(em.getReference(Hall.class, hallId));
                s.setRowNumber(r);
                s.setSeatNumber(n);
                if (overwriteLabels || s.getLabel() == null) {
                    s.setLabel((char)('A' + (r - 1)) + "-" + n); // npr. A-1, B-12...
                }
                seatRepository.save(s);
                created.add(seatMapper.toDto(s));
            }
        }
        return created;
    }

    // ——— Validacija ———
    private void validate(SeatDto dto, boolean isUpdate) throws Exception {
        if (dto == null) throw new Exception("DTO je null!");
        if (!isUpdate && dto.getId() != null) throw new Exception("ID se ne prosleđuje pri kreiranju.");
        if (dto.getHallId() == null) throw new Exception("hallId je obavezan.");
        if (dto.getRowNumber() < 1) throw new Exception("rowNumber mora biti >= 1.");
        if (dto.getSeatNumber() < 1) throw new Exception("seatNumber mora biti >= 1.");
        if (dto.getLabel() != null && dto.getLabel().length() > 20) throw new Exception("label max 20 karaktera.");
    }
}
