package com.mycompany.njtspringbioskop.servis;

import com.mycompany.njtspringbioskop.dto.impl.ReservationDto;
import com.mycompany.njtspringbioskop.entity.impl.Projection;
import com.mycompany.njtspringbioskop.entity.impl.Reservation;
import com.mycompany.njtspringbioskop.entity.impl.User;
import com.mycompany.njtspringbioskop.mapper.impl.ReservationMapper;
import com.mycompany.njtspringbioskop.repository.impl.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServis {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @PersistenceContext
    private EntityManager em;

    public ReservationServis(ReservationRepository reservationRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    // ——— CRUD ———

    public List<ReservationDto> findAll() {
        return reservationRepository.findAll()
                .stream().map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReservationDto findById(Long id) throws Exception {
        return reservationMapper.toDto(reservationRepository.findById(id));
    }

    public ReservationDto create(ReservationDto dto) throws Exception {
        validate(dto, false);

        // Verifikuj da user i projection postoje (jasna poruka)
        User user = em.find(User.class, dto.getUserId());
        if (user == null) throw new Exception("Korisnik ne postoji (userId=" + dto.getUserId() + ").");
        Projection proj = em.find(Projection.class, dto.getProjectionId());
        if (proj == null) throw new Exception("Projekcija ne postoji (projectionId=" + dto.getProjectionId() + ").");

        Reservation r = reservationMapper.toEntity(dto);
        // Zakači reference bez dodatnih SELECT-a
        r.setUser(em.getReference(User.class, dto.getUserId()));
        r.setProjection(em.getReference(Projection.class, dto.getProjectionId()));
        // NOTE: r.getReservedAt() ostaje kako je (entitet default now ili iz DTO)

        reservationRepository.save(r);
        return reservationMapper.toDto(r);
    }

    public ReservationDto update(Long id, ReservationDto dto) throws Exception {
        if (id == null) throw new Exception("ID je obavezan.");
        dto.setId(id);
        validate(dto, true);

        // Baci grešku ako ne postoji
        reservationRepository.findById(id);

        // Proveri da postoje povezane reference
        User user = em.find(User.class, dto.getUserId());
        if (user == null) throw new Exception("Korisnik ne postoji (userId=" + dto.getUserId() + ").");
        Projection proj = em.find(Projection.class, dto.getProjectionId());
        if (proj == null) throw new Exception("Projekcija ne postoji (projectionId=" + dto.getProjectionId() + ").");

        Reservation r = reservationMapper.toEntity(dto);
        r.setUser(em.getReference(User.class, dto.getUserId()));
        r.setProjection(em.getReference(Projection.class, dto.getProjectionId()));

        reservationRepository.save(r);
        return reservationMapper.toDto(r);
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    // ——— Filteri ———

    public List<ReservationDto> findByUser(Long userId) {
        return reservationRepository.findByUser(userId)
                .stream().map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservationDto> findByProjection(Long projectionId) {
        return reservationRepository.findByProjection(projectionId)
                .stream().map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservationDto> findInRange(LocalDateTime from, LocalDateTime to) {
        return reservationRepository.findInRange(from, to)
                .stream().map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public long reservedTicketsForProjection(Long projectionId) {
        return reservationRepository.sumTicketsForProjection(projectionId);
    }

    // ——— Validacija ———
    private void validate(ReservationDto dto, boolean isUpdate) throws Exception {
        if (dto == null) throw new Exception("DTO je null!");
        if (!isUpdate && dto.getId() != null) throw new Exception("ID se ne prosleđuje pri kreiranju.");
        if (dto.getNumberOfTickets() < 1) throw new Exception("numberOfTickets mora biti >= 1.");
        if (dto.getUserId() == null) throw new Exception("userId je obavezan.");
        if (dto.getProjectionId() == null) throw new Exception("projectionId je obavezan.");
        // reservedAt nije obavezan (entitet postavlja now), ali ako stigne — prihvatićemo ga.
    }
}
