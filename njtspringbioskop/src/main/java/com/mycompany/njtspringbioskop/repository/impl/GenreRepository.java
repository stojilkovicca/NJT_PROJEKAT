/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.repository.impl;

import com.mycompany.njtspringbioskop.entity.impl.Genre;
import com.mycompany.njtspringbioskop.repository.MyAppRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List; 
import org.springframework.stereotype.Repository;

@Repository
public class GenreRepository implements MyAppRepository<Genre, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        return entityManager
                .createQuery("SELECT g FROM Genre g ORDER BY g.name", Genre.class)
                .getResultList();
    }

    @Override
    public Genre findById(Long id) throws Exception {
        Genre g = entityManager.find(Genre.class, id);
        if (g == null) throw new Exception("Žanr nije pronađen!");
        return g;
    }

    @Override
    @Transactional
    public void save(Genre entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Genre g = entityManager.find(Genre.class, id);
        if (g != null) entityManager.remove(g);
    }

    // Dodatno (nije deo MyAppRepository interfejsa, ali možeš koristiti iz servisa):
    public boolean existsByNameIgnoreCase(String name) {
        Long cnt = entityManager.createQuery(
                "SELECT COUNT(g) FROM Genre g WHERE LOWER(g.name) = LOWER(:name)", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return cnt != null && cnt > 0;
    }
}