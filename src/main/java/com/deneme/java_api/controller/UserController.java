package com.deneme.java_api.controller;

import com.deneme.java_api.dto.ApiResponse;
import com.deneme.java_api.dto.UserRequest;
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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setSurname(request.getSurname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "ROLE_USER");
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User created successfully.", savedUser));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Users listed successfully.", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success("User found.", user)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("User not found with id: " + id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id,
            @RequestBody UserRequest request) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(request.getUsername());
            user.setSurname(request.getSurname());
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (request.getRole() != null) {
                user.setRole(request.getRole());
            }
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully.", updatedUser));
        }).orElse(ResponseEntity.status(404).body(ApiResponse.error("User not found with id: " + id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully.", null));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("User not found with id: " + id));
    }
}