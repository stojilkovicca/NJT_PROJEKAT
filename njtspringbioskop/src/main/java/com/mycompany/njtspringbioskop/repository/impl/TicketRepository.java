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

}
