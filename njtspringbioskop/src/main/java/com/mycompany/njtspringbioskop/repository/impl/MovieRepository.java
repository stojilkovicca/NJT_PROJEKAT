/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.repository.impl;

import com.mycompany.njtspringbioskop.entity.impl.Movie;
import com.mycompany.njtspringbioskop.repository.MyAppRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List; 
import org.springframework.stereotype.Repository;

@Repository
public class MovieRepository implements MyAppRepository<Movie, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Movie> findAll() {
        return entityManager
                .createQuery("SELECT m FROM Movie m ORDER BY m.title", Movie.class)
                .getResultList();
    }

    @Override
    public Movie findById(Long id) throws Exception {
        Movie m = entityManager.find(Movie.class, id);
        if (m == null) throw new Exception("Film nije pronađen!");
        return m;
    }

    @Override
    @Transactional
    public void save(Movie entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Movie m = entityManager.find(Movie.class, id);
        if (m != null) entityManager.remove(m);
    }

    // Dodatno (korisno za validaciju duplikata)
    public boolean existsByTitleIgnoreCase(String title) {
        Long cnt = entityManager.createQuery(
                "SELECT COUNT(m) FROM Movie m WHERE LOWER(m.title) = LOWER(:t)", Long.class)
            .setParameter("t", title)
            .getSingleResult();
        return cnt != null && cnt > 0;
    }
}