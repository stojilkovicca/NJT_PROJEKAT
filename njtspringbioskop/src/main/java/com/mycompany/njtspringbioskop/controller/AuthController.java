package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.auth.LoginRequest;
import com.mycompany.njtspringbioskop.dto.auth.LoginResponse;
import com.mycompany.njtspringbioskop.dto.auth.RegisterRequest;
import com.mycompany.njtspringbioskop.entity.impl.Role;
import com.mycompany.njtspringbioskop.entity.impl.User;
import com.mycompany.njtspringbioskop.repository.UserRepository;
import com.mycompany.njtspringbioskop.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (users.existsByUsername(req.getUsername()))
            return ResponseEntity.badRequest().body("Username taken");
        if (users.existsByEmail(req.getEmail()))
            return ResponseEntity.badRequest().body("Email taken");

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole(Role.USER);
        users.save(u);

        String token = jwt.generate(u.getUsername(), u.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, u.getRole().name(), u.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User u = users.findByUsername(req.getUsername()).orElse(null);
        if (u == null || !encoder.matches(req.getPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(401).body("Bad credentials");
        }

        String token = jwt.generate(u.getUsername(), u.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, u.getRole().name(), u.getUsername()));
    }
}
