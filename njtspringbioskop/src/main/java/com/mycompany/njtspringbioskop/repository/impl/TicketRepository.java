package com.mycompany.njtspringbioskop.repository.impl;

import com.mycompany.njtspringbioskop.entity.impl.Ticket;
import com.mycompany.njtspringbioskop.repository.MyAppRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketRepository implements MyAppRepository<Ticket, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Ticket> findAll() {
        return em.createQuery(
                "SELECT t FROM Ticket t ORDER BY t.id DESC",
                Ticket.class
        ).getResultList();
    }

    @Override
    public Ticket findById(Long id) throws Exception {
        Ticket t = em.find(Ticket.class, id);
        if (t == null) throw new Exception("Karta nije pronađena!");
        return t;
    }

    @Override
    @Transactional
    public void save(Ticket entity) {
        if (entity.getId() == null) em.persist(entity);
        else em.merge(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Ticket t = em.find(Ticket.class, id);
        if (t != null) em.remove(t);
    }

    // ——— Dodatni upiti ———

    public boolean existsByProjectionAndSeat(Long projectionId, Long seatId) {
        Long cnt = em.createQuery(
                "SELECT COUNT(t) FROM Ticket t WHERE t.projection.id = :pid AND t.seat.id = :sid",
                Long.class
        ).setParameter("pid", projectionId)
         .setParameter("sid", seatId)
         .getSingleResult();
        return cnt != null && cnt > 0;
    }

    public Ticket findByQrCode(String qr) {
        List<Ticket> list = em.createQuery(
                "SELECT t FROM Ticket t WHERE t.qrCode = :qr",
                Ticket.class
        ).setParameter("qr", qr).setMaxResults(1).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Ticket> findByProjection(Long projectionId) {
        return em.createQuery(
                "SELECT t FROM Ticket t WHERE t.projection.id = :pid ORDER BY t.id DESC",
                Ticket.class
        ).setParameter("pid", projectionId).getResultList();
    }

    public List<Ticket> findByReservation(Long reservationId) {
        return em.createQuery(
                "SELECT t FROM Ticket t WHERE t.reservation.id = :rid ORDER BY t.id DESC",
                Ticket.class
        ).setParameter("rid", reservationId).getResultList();
    }

    public List<Ticket> findBySeat(Long seatId) {
        return em.createQuery(
                "SELECT t FROM Ticket t WHERE t.seat.id = :sid ORDER BY t.id DESC",
                Ticket.class
        ).setParameter("sid", seatId).getResultList();
    }
}
