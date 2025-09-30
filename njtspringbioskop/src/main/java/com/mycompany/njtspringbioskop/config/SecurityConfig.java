
package com.mycompany.njtspringbioskop.config;

import com.mycompany.njtspringbioskop.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()   // <<< SVE JAVNO
            );

        // Ne dodajemo JwtAuthFilter niti bilo kakve auth filtere
        return http.build();
    }
    
    /*
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // preflight, error, swagger, auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/reservation/**").permitAll()
            // javni GET-ovi
            .requestMatchers(HttpMethod.GET,
                    "/api/movie/**",
                    "/api/projection/**",
                    "/api/hall/**",
                    "/api/seat/**",
                    "/api/ticket/**",
                    "/api/genre/**"
            ).permitAll()

            // --- REZERVACIJE (specifična pravila) ---
            // kreiranje rezervacije: USER ili ADMIN
            .requestMatchers(HttpMethod.POST, "/api/reservation/**")
                .hasAnyRole("USER","ADMIN")
            // pregled sopstvenih rezervacija: USER ili ADMIN
            .requestMatchers(HttpMethod.GET, "/api/reservation/user/**")
                .hasAnyRole("USER","ADMIN")
            // sve ostale GET/PUT/DELETE nad rezervacijama – samo ADMIN
            .requestMatchers("/api/reservation/**").hasRole("ADMIN")

            // --- GENERIČNA ADMIN PRAVILA (ostaju na dnu) ---
            .requestMatchers(HttpMethod.POST,   "/api/**").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.PUT,    "/api/**").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("ADMIN")

            // sve ostalo traži prijavu
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}*/

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
