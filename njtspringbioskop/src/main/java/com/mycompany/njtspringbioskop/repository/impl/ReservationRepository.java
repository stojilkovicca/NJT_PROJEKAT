// com/mycompany/njtspringbioskop/repository/impl/ReservationRepository.java
package com.mycompany.njtspringbioskop.repository.impl;

import com.mycompany.njtspringbioskop.entity.impl.Reservation;
import com.mycompany.njtspringbioskop.repository.MyAppRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReservationRepository implements MyAppRepository<Reservation, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Reservation> findAll() {
        return em.createQuery(
                "SELECT r FROM Reservation r ORDER BY r.reservedAt DESC",
                Reservation.class
        ).getResultList();
    }

    @Override
    public Reservation findById(Long id) throws Exception {
        Reservation r = em.find(Reservation.class, id);
        if (r == null) throw new Exception("Rezervacija nije pronađena!");
        return r;
    }

    @Override
    @Transactional
    public void save(Reservation entity) {
        if (entity.getId() == null) em.persist(entity);
        else em.merge(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Reservation r = em.find(Reservation.class, id);
        if (r != null) em.remove(r);
    }

    // ——— Dodatni upiti ———

    public List<Reservation> findByUser(Long userId) {
        return em.createQuery(
                "SELECT r FROM Reservation r WHERE r.user.id = :uid ORDER BY r.reservedAt DESC",
                Reservation.class
        ).setParameter("uid", userId).getResultList();
    }

    public List<Reservation> findByProjection(Long projectionId) {
        return em.createQuery(
                "SELECT r FROM Reservation r WHERE r.projection.id = :pid ORDER BY r.reservedAt DESC",
                Reservation.class
        ).setParameter("pid", projectionId).getResultList();
    }

    public List<Reservation> findInRange(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                "SELECT r FROM Reservation r WHERE r.reservedAt BETWEEN :from AND :to ORDER BY r.reservedAt DESC",
                Reservation.class
        ).setParameter("from", from)
         .setParameter("to", to)
         .getResultList();
    }

    public long sumTicketsForProjection(Long projectionId) {
        Long sum = em.createQuery(
                "SELECT COALESCE(SUM(r.numberOfTickets),0) FROM Reservation r WHERE r.projection.id = :pid",
                Long.class
        ).setParameter("pid", projectionId).getSingleResult();
        return sum == null ? 0L : sum;
    }
}
