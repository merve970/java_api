package com.deneme.java_api.controller;

import com.deneme.java_api.dto.LoginRequest;
import com.deneme.java_api.dto.LoginResponse;
import com.deneme.java_api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String token = jwtUtil.generateToken(auth.getName(), role);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}