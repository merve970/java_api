package com.deneme.java_api.service;

import com.deneme.java_api.dto.UserRequest;
import com.deneme.java_api.entity.Role;
import com.deneme.java_api.entity.User;
import com.deneme.java_api.repository.RoleRepository;
import com.deneme.java_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isValidRole(String roleName) {
        return List.of("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EMPLOYEE").contains(roleName);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User register(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setSurname(request.getSurname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        String roleName = request.getRole() != null ? request.getRole() : "ROLE_EMPLOYEE";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        user.setRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getSurname() != null) user.setSurname(request.getSurname());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        if (request.getRole() != null) {
            Role role = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateRole(Long id, String newRoleName) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return userRepository.existsByUsernameAndIdNot(username, id);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}