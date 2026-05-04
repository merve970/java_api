package com.deneme.java_api.controller;

import com.deneme.java_api.dto.ApiResponse;
import com.deneme.java_api.dto.LoginRequest;
import com.deneme.java_api.dto.LoginResponse;
import com.deneme.java_api.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 1. Bearer Login: Kullanıcı adı ve şifre ile Token alır (8080 Portu için)
    @Operation(summary = "JWT ile giriş yap ve token al")
    @PostMapping("/login/bearer")
    public ResponseEntity<ApiResponse<LoginResponse>> loginBearer(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));
        
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String token = jwtUtil.generateToken(auth.getName(), role);
        
        return ResponseEntity.ok(ApiResponse.success("Bearer login successful", new LoginResponse(token)));
    }

    // 2. Basic Login: 8081 portu üzerinden yapılan Basic Auth girişini doğrular
    @Operation(summary = "Basic Auth ile giriş yap")
    @PostMapping("/login/basic")
    public ResponseEntity<ApiResponse<String>> loginBasic() {
        // Bu endpoint'e ulaşıldıysa Basic Auth filtresi kullanıcıyı zaten doğrulamıştır
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        return ResponseEntity.ok(ApiResponse.success("Basic login successful for user: " + auth.getName(), null));
    }
}