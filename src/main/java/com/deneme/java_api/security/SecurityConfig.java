package com.deneme.java_api.security;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtFilter jwtFilter;

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .servers(java.util.List.of(
                                                new io.swagger.v3.oas.models.servers.Server()
                                                                .url("http://localhost:8080").description("JWT Portu"),
                                                new io.swagger.v3.oas.models.servers.Server()
                                                                .url("http://localhost:8081")
                                                                .description("Basic Auth Portu")))
                                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                                                .addList("Bearer Auth").addList("Basic Auth"))
                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes("Bearer Auth",
                                                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                                                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer").bearerFormat("JWT"))
                                                .addSecuritySchemes("Basic Auth",
                                                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                                                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                                                                .scheme("basic")));
        }

        // 1. ZİNCİR: 8081 Portu (Basic Auth)
        @Bean
        @Order(1)
        public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher(request -> request.getLocalPort() == 8081)
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                                .httpBasic(org.springframework.security.config.Customizer.withDefaults());
                return http.build();
        }

        // 2. ZİNCİR: 8080 Portu (JWT/Bearer)
        @Bean
        @Order(2)
        public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Yeni eklenen giriş yollarına izin ver
                                                .requestMatchers("/auth/login/bearer", "/auth/login/basic").permitAll()
                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                        throws Exception {
                return config.getAuthenticationManager();
        }
}