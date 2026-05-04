package com.deneme.java_api.controller;

import com.deneme.java_api.dto.ApiResponse;
import com.deneme.java_api.dto.RoleUpdateRequest;
import com.deneme.java_api.dto.UserRequest;
import com.deneme.java_api.entity.User;
import com.deneme.java_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody UserRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) 
            return ResponseEntity.status(400).body(ApiResponse.error("Username is required."));
        if (request.getPassword() == null || request.getPassword().isBlank()) 
            return ResponseEntity.status(400).body(ApiResponse.error("Password is required."));
        
        if (request.getRole() != null && !userService.isValidRole(request.getRole())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Invalid role."));
        }

        return ResponseEntity.ok(ApiResponse.success("User created.", userService.register(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Listed.", userService.findAll()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        if (request.getRole() != null && !userService.isValidRole(request.getRole())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Invalid role."));
        }

        if (request.getUsername() != null && userService.existsByUsernameAndIdNot(request.getUsername(), id)) {
            return ResponseEntity.status(409).body(ApiResponse.error("Username already taken."));
        }

        return ResponseEntity.ok(ApiResponse.success("Updated.", userService.update(id, request)));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest roleRequest) {
        if (roleRequest.getRole() == null || !userService.isValidRole(roleRequest.getRole())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Invalid role."));
        }
        return ResponseEntity.ok(ApiResponse.success("Role updated.", userService.updateRole(id, roleRequest.getRole())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        
        return userService.findById(id).map(user -> {
            if (user.getUsername().equals(currentUsername)) {
                return ResponseEntity.status(400).body(ApiResponse.<Void>error("You cannot delete yourself!"));
            }
            userService.delete(id);
            return ResponseEntity.ok(ApiResponse.<Void>success("Deleted.", null));
        }).orElse(ResponseEntity.status(404).body(ApiResponse.error("User not found.")));
    }
}