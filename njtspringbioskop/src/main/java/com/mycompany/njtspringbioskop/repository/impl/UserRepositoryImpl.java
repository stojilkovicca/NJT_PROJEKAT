package com.mycompany.njtspringbioskop.repository.impl;

import com.mycompany.njtspringbioskop.entity.impl.User;
import com.mycompany.njtspringbioskop.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            User u = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
            return Optional.of(u);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        Long cnt = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
            .setParameter("username", username)
            .getSingleResult();
        return cnt != null && cnt > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Long cnt = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
            .setParameter("email", email)
            .getSingleResult();
        return cnt != null && cnt > 0;
    }

    // NOVO: findByEmail
    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User u = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
            return Optional.of(u);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    // NOVO: findByVerificationToken
    @Override
    public Optional<User> findByVerificationToken(String token) {
        try {
            User u = em.createQuery(
                "SELECT u FROM User u WHERE u.verificationToken = :t", User.class)
                .setParameter("t", token)
                .getSingleResult();
            return Optional.of(u);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }
}
