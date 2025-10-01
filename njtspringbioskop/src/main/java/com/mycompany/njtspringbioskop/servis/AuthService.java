package com.mycompany.njtspringbioskop.servis;

import com.mycompany.njtspringbioskop.dto.auth.LoginRequest;
import com.mycompany.njtspringbioskop.dto.auth.LoginResponse;
import com.mycompany.njtspringbioskop.dto.auth.RegisterRequest;
import com.mycompany.njtspringbioskop.entity.impl.Role;
import com.mycompany.njtspringbioskop.entity.impl.User;
import com.mycompany.njtspringbioskop.repository.UserRepository;
import com.mycompany.njtspringbioskop.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    private final MailService mail;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtUtil jwt, MailService mail) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.mail = mail;
    }

    public LoginResponse register(RegisterRequest req) {
        if (users.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username taken");
        }
        if (users.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email taken");
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole(Role.USER);

        // verifikacioni token
        String token = UUID.randomUUID().toString();
        u.setVerificationToken(token);
        u.setVerificationTokenExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        u.setEmailVerified(false);

        users.save(u);

        // šaljemo mejl
        mail.sendVerificationEmail(u.getEmail(), token);

        // možeš vratiti "info" odgovor; ovde vraćam isti tip kao login da ti front ne menjaš
        String jwtToken = jwt.generate(u.getUsername(), u.getRole().name());
        return new LoginResponse(jwtToken, u.getRole().name(), u.getUsername(), u.getId());
    }

    public String verify(String token) {
        User u = users.findByVerificationToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token"));

        if (u.isEmailVerified()) {
            return "E-mail je već verifikovan.";
        }
        if (u.getVerificationTokenExpiresAt() == null || u.getVerificationTokenExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Token je istekao. Zatražite novi verifikacioni mejl.");
        }

        u.setEmailVerified(true);
        u.setVerificationToken(null);
        u.setVerificationTokenExpiresAt(null);
        users.save(u);

        return "E-mail uspešno verifikovan.";
    }

    public String resendVerification(String email) {
        User u = users.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Korisnik sa tim e-mailom ne postoji"));

        if (u.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail je već verifikovan.");
        }

        String token = UUID.randomUUID().toString();
        u.setVerificationToken(token);
        u.setVerificationTokenExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        users.save(u);

        mail.sendVerificationEmail(u.getEmail(), token);
        return "Poslat je novi verifikacioni mejl.";
    }

    public LoginResponse login(LoginRequest req) {
        User u = users.findByUsername(req.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials"));

        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }

        if (!u.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "E-mail nije verifikovan. Proverite poštu ili zatražite novi mejl.");
        }

        String token = jwt.generate(u.getUsername(), u.getRole().name());
        return new LoginResponse(token, u.getRole().name(), u.getUsername(), u.getId());
    }
}
