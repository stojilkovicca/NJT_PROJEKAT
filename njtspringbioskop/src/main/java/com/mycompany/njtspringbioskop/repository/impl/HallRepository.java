/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.repository.impl;

import com.mycompany.njtspringbioskop.entity.impl.Hall;
import com.mycompany.njtspringbioskop.entity.impl.Seat;
import com.mycompany.njtspringbioskop.repository.MyAppRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List; 
import org.springframework.stereotype.Repository;

@Repository
public class HallRepository implements MyAppRepository<Hall, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Hall> findAll() {
        return entityManager
            .createQuery("SELECT h FROM Hall h ORDER BY h.name", Hall.class)
            .getResultList();
    }

    @Override
    public Hall findById(Long id) throws Exception {
        Hall h = entityManager.find(Hall.class, id);
        if (h == null) throw new Exception("Sala nije pronađena!");
        return h;
    }

    @Override
    @Transactional
    public void save(Hall entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Hall h = entityManager.find(Hall.class, id);
        if (h != null) entityManager.remove(h);
    }

    // -------- dodatne pomoćne metode za sedišta --------
 
    public boolean seatExists(Long hallId, int rowNumber, int seatNumber) {
        Long cnt = entityManager.createQuery(
                "SELECT COUNT(s) FROM Seat s " +
                "WHERE s.hall.id = :hid AND s.rowNumber = :r AND s.seatNumber = :c",
                Long.class)
            .setParameter("hid", hallId)
            .setParameter("r", rowNumber)
            .setParameter("c", seatNumber)
            .getSingleResult();
        return cnt != null && cnt > 0;
    }
 
    @Transactional
    public void saveSeat(Seat s) {
        if (s.getId() == null) {
            entityManager.persist(s);
        } else {
            entityManager.merge(s);
        }
    }
 
    public List<Seat> findSeatsByHall(Long hallId) {
        return entityManager.createQuery(
                "SELECT s FROM Seat s WHERE s.hall.id = :hid ORDER BY s.rowNumber, s.seatNumber",
                Seat.class)
            .setParameter("hid", hallId)
            .getResultList();
    }
}
