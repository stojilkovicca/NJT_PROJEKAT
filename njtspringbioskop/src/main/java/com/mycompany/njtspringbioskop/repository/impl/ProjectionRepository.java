package com.mycompany.njtspringbioskop.repository.impl;

import com.mycompany.njtspringbioskop.entity.impl.Projection;
import com.mycompany.njtspringbioskop.repository.MyAppRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProjectionRepository implements MyAppRepository<Projection, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Projection> findAll() {
        return em.createQuery(
                "SELECT p FROM Projection p ORDER BY p.dateTime DESC",
                Projection.class
        ).getResultList();
    }

    @Override
    public Projection findById(Long id) throws Exception {
        Projection p = em.find(Projection.class, id);
        if (p == null) throw new Exception("Projekcija nije pronađena!");
        return p;
    }

    @Override
    @Transactional
    public void save(Projection entity) {
        if (entity.getId() == null) em.persist(entity);
        else em.merge(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Projection p = em.find(Projection.class, id);
        if (p != null) em.remove(p);
    }

    // Dodatni korisni upiti (bez komplikovanja)
    public List<Projection> findByHall(Long hallId) {
        return em.createQuery(
                "SELECT p FROM Projection p WHERE p.hall.id = :hid ORDER BY p.dateTime",
                Projection.class
        ).setParameter("hid", hallId).getResultList();
    }

    public List<Projection> findByMovie(Long movieId) {
        return em.createQuery(
                "SELECT p FROM Projection p WHERE p.movie.id = :mid ORDER BY p.dateTime",
                Projection.class
        ).setParameter("mid", movieId).getResultList();
    }

    public List<Projection> findInRange(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                "SELECT p FROM Projection p WHERE p.dateTime BETWEEN :from AND :to ORDER BY p.dateTime",
                Projection.class
        ).setParameter("from", from)
         .setParameter("to", to)
         .getResultList();
    }
}
