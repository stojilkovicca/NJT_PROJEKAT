/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.repository;

import com.mycompany.njtspringbioskop.entity.impl.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User save(User user);
    
    
     // DODATO za slanje mejla putem registracije
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
}
