package com.deneme.java_api.controller;

import com.deneme.java_api.entity.User;
import com.deneme.java_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- CREATE (POST) ---
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Not: Gerçek projede burada UserRequest DTO'su kullanılır ve şifre
        // Bcryptlenir.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // --- READ ALL (GET) ---
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // --- READ ONE (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- UPDATE (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setSurname(userDetails.getSurname());
            user.setRole(userDetails.getRole());
            // Şifre güncellenecekse burada tekrar encode edilmeli
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- DELETE (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}