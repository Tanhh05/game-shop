package com.example.gameshopbackend.config;

import com.example.gameshopbackend.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"error\":\"Forbidden\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/ping", "/api/orders/ping").permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/games/**", "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/webhook/sepay").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/games", "/api/products", "/api/files/upload").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/games/**", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/games/**", "/api/inventory/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasRole("ADMIN")

                        // Client endpoints (USER/RESELLER/ADMIN)
                        .requestMatchers("/api/auth/logout", "/api/auth/refresh-token").authenticated()
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "RESELLER", "ADMIN")
                        .requestMatchers("/api/orders/history", "/api/orders/buy-now").hasAnyRole("USER", "RESELLER", "ADMIN")
                        .requestMatchers("/api/wallet/**").hasAnyRole("USER", "RESELLER", "ADMIN")
                        .requestMatchers("/api/payment/**").hasAnyRole("USER", "RESELLER", "ADMIN")
                        .requestMatchers("/api/paypal/**").hasAnyRole("USER", "RESELLER", "ADMIN")
                        .requestMatchers("/api/webhook/wallet/deposit-info").hasAnyRole("USER", "RESELLER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
