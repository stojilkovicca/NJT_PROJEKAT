package com.mycompany.njtspringbioskop.servis;

import com.mycompany.njtspringbioskop.dto.impl.TicketDto;
import com.mycompany.njtspringbioskop.entity.impl.*;
import com.mycompany.njtspringbioskop.mapper.impl.TicketMapper;
import com.mycompany.njtspringbioskop.repository.impl.TicketRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketServis {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @PersistenceContext
    private EntityManager em;

    public TicketServis(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    // ——— CRUD ———

    public List<TicketDto> findAll() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toDto).collect(Collectors.toList());
    }

    public TicketDto findById(Long id) throws Exception {
        return ticketMapper.toDto(ticketRepository.findById(id));
    }

    public TicketDto create(TicketDto dto) throws Exception {
        validate(dto, false);

        // učitaj reference radi validacija
        Projection p = em.find(Projection.class, dto.getProjectionId());
        if (p == null) throw new Exception("Projekcija ne postoji (projectionId=" + dto.getProjectionId() + ").");

        Seat s = em.find(Seat.class, dto.getSeatId());
        if (s == null) throw new Exception("Sedište ne postoji (seatId=" + dto.getSeatId() + ").");

        Reservation r = em.find(Reservation.class, dto.getReservationId());
        if (r == null) throw new Exception("Rezervacija ne postoji (reservationId=" + dto.getReservationId() + ").");

        // seat mora biti iz iste hale kao projekcija
        if (s.getHall() == null || p.getHall() == null || !s.getHall().getId().equals(p.getHall().getId())) {
            throw new Exception("Sedište nije u istoj sali kao projekcija.");
        }

        // rezervacija mora da bude za istu projekciju
        if (r.getProjection() == null || !r.getProjection().getId().equals(p.getId())) {
            throw new Exception("Rezervacija nije za datu projekciju.");
        }

        // da li je mesto već prodato za tu projekciju?
        if (ticketRepository.existsByProjectionAndSeat(dto.getProjectionId(), dto.getSeatId())) {
            throw new Exception("Mesto je već zauzeto (već postoji karta za projekciju i sedište).");
        }

        // default cena = basePrice projekcije ako nije zadato
        if (dto.getTicketPrice() <= 0) {
            dto.setTicketPrice(p.getBasePrice());
        }

        // ako nema qrCode, generiši
        if (dto.getQrCode() == null || dto.getQrCode().isBlank()) {
            dto.setQrCode(generateQr(dto));
        } else {
            // uniqueness check (uk_ticket_qrcode)
            if (ticketRepository.findByQrCode(dto.getQrCode()) != null) {
                throw new Exception("QR kod već postoji.");
            }
        }

        Ticket t = ticketMapper.toEntity(dto);
        t.setProjection(em.getReference(Projection.class, dto.getProjectionId()));
        t.setSeat(em.getReference(Seat.class, dto.getSeatId()));
        t.setReservation(em.getReference(Reservation.class, dto.getReservationId()));

        ticketRepository.save(t);
        return ticketMapper.toDto(t);
    }

    public TicketDto update(Long id, TicketDto dto) throws Exception {
        if (id == null) throw new Exception("ID je obavezan.");
        dto.setId(id);
        validate(dto, true);

        // mora postojati
        Ticket existing = ticketRepository.findById(id);

        Projection p = em.find(Projection.class, dto.getProjectionId());
        if (p == null) throw new Exception("Projekcija ne postoji.");
        Seat s = em.find(Seat.class, dto.getSeatId());
        if (s == null) throw new Exception("Sedište ne postoji.");
        Reservation r = em.find(Reservation.class, dto.getReservationId());
        if (r == null) throw new Exception("Rezervacija ne postoji.");

        if (s.getHall() == null || p.getHall() == null || !s.getHall().getId().equals(p.getHall().getId())) {
            throw new Exception("Sedište nije u istoj sali kao projekcija.");
        }
        if (r.getProjection() == null || !r.getProjection().getId().equals(p.getId())) {
            throw new Exception("Rezervacija nije za datu projekciju.");
        }

        // ako se promeni par (projection, seat), proveri zauzeće
        boolean projectionChanged = !existing.getProjection().getId().equals(dto.getProjectionId());
        boolean seatChanged = !existing.getSeat().getId().equals(dto.getSeatId());
        if ((projectionChanged || seatChanged) &&
            ticketRepository.existsByProjectionAndSeat(dto.getProjectionId(), dto.getSeatId())) {
            throw new Exception("Mesto je već zauzeto za datu projekciju.");
        }

        // QR handling: ako se menja, proveri jedinstvenost
        if (dto.getQrCode() != null && !dto.getQrCode().isBlank()) {
            Ticket other = ticketRepository.findByQrCode(dto.getQrCode());
            if (other != null && !other.getId().equals(id)) {
                throw new Exception("QR kod već postoji.");
            }
        } else if (existing.getQrCode() == null || existing.getQrCode().isBlank()) {
            dto.setQrCode(generateQr(dto));
        }

        Ticket t = ticketMapper.toEntity(dto);
        t.setProjection(em.getReference(Projection.class, dto.getProjectionId()));
        t.setSeat(em.getReference(Seat.class, dto.getSeatId()));
        t.setReservation(em.getReference(Reservation.class, dto.getReservationId()));

        ticketRepository.save(t);
        return ticketMapper.toDto(t);
    }

    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }

    // ——— Filteri ———

    public TicketDto findByQrCode(String qr) throws Exception {
        Ticket t = ticketRepository.findByQrCode(qr);
        if (t == null) throw new Exception("Karta sa datim QR kodom ne postoji.");
        return ticketMapper.toDto(t);
    }

    public List<TicketDto> findByProjection(Long projectionId) {
        return ticketRepository.findByProjection(projectionId).stream()
                .map(ticketMapper::toDto).collect(Collectors.toList());
    }

    public List<TicketDto> findByReservation(Long reservationId) {
        return ticketRepository.findByReservation(reservationId).stream()
                .map(ticketMapper::toDto).collect(Collectors.toList());
    }

    public List<TicketDto> findBySeat(Long seatId) {
        return ticketRepository.findBySeat(seatId).stream()
                .map(ticketMapper::toDto).collect(Collectors.toList());
    }

    // ——— Helpers ———
    private void validate(TicketDto dto, boolean isUpdate) throws Exception {
        if (dto == null) throw new Exception("DTO je null!");
        if (!isUpdate && dto.getId() != null) throw new Exception("ID se ne prosleđuje pri kreiranju.");
        if (dto.getTicketPrice() < 0) throw new Exception("ticketPrice ne može biti negativan.");
        if (dto.getProjectionId() == null) throw new Exception("projectionId je obavezan.");
        if (dto.getSeatId() == null) throw new Exception("seatId je obavezan.");
        if (dto.getReservationId() == null) throw new Exception("reservationId je obavezan.");
        if (dto.getQrCode() != null && dto.getQrCode().length() > 120) {
            throw new Exception("qrCode max 120 karaktera.");
        }
    }

    private String generateQr(TicketDto dto) {
        // jednostavan QR payload — po želji promeni format
        return "QR-" + dto.getProjectionId() + "-" + dto.getSeatId() + "-" + UUID.randomUUID();
    }
}
