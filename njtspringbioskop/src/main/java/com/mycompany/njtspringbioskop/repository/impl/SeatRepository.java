
package com.mycompany.njtspringbioskop.repository.impl;

import com.mycompany.njtspringbioskop.entity.impl.Seat;
import com.mycompany.njtspringbioskop.repository.MyAppRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeatRepository implements MyAppRepository<Seat, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Seat> findAll() {
        return em.createQuery(
                "SELECT s FROM Seat s ORDER BY s.hall.id, s.rowNumber, s.seatNumber",
                Seat.class
        ).getResultList();
    }

    @Override
    public Seat findById(Long id) throws Exception {
        Seat s = em.find(Seat.class, id);
        if (s == null) throw new Exception("Sedište nije pronađeno!");
        return s;
    }

    @Override
    @Transactional
    public void save(Seat entity) {
        if (entity.getId() == null) em.persist(entity);
        else em.merge(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Seat s = em.find(Seat.class, id);
        if (s != null) em.remove(s);
    }

    // ——— Dodatni upiti ———

    public boolean existsByHallRowSeat(Long hallId, int rowNum, int seatNum) {
        Long cnt = em.createQuery(
                "SELECT COUNT(s) FROM Seat s WHERE s.hall.id = :hid AND s.rowNumber = :r AND s.seatNumber = :n",
                Long.class
        ).setParameter("hid", hallId)
         .setParameter("r", rowNum)
         .setParameter("n", seatNum)
         .getSingleResult();
        return cnt != null && cnt > 0;
    }

    public List<Seat> findByHall(Long hallId) {
        return em.createQuery(
                "SELECT s FROM Seat s WHERE s.hall.id = :hid ORDER BY s.rowNumber, s.seatNumber",
                Seat.class
        ).setParameter("hid", hallId).getResultList();
    }

    public List<Seat> findByHallAndRow(Long hallId, int rowNum) {
        return em.createQuery(
                "SELECT s FROM Seat s WHERE s.hall.id = :hid AND s.rowNumber = :r ORDER BY s.seatNumber",
                Seat.class
        ).setParameter("hid", hallId)
         .setParameter("r", rowNum)
         .getResultList();
    }

    public List<Seat> searchByHallAndLabel(Long hallId, String q) {
        return em.createQuery(
                "SELECT s FROM Seat s WHERE s.hall.id = :hid AND LOWER(s.label) LIKE LOWER(CONCAT('%', :q, '%')) " +
                "ORDER BY s.rowNumber, s.seatNumber",
                Seat.class
        ).setParameter("hid", hallId)
         .setParameter("q", q == null ? "" : q)
         .getResultList();
    }
}
