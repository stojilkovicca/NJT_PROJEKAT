package com.mycompany.njtspringbioskop.controller;

import com.mycompany.njtspringbioskop.dto.auth.LoginRequest;
import com.mycompany.njtspringbioskop.dto.auth.LoginResponse;
import com.mycompany.njtspringbioskop.dto.auth.RegisterRequest;
import com.mycompany.njtspringbioskop.servis.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest req) {
        // vraćamo LoginResponse da ne menjaš front (možeš i poruku “proveri mejl”)
        return ResponseEntity.ok(auth.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("OK");
    }

    // GET /api/auth/verify?token=...
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        return ResponseEntity.ok(auth.verify(token));
    }

    // POST /api/auth/resend-verification?email=...
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resend(@RequestParam String email) {
        return ResponseEntity.ok(auth.resendVerification(email));
    }
}
