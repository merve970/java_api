package com.deneme.java_api.repository;

import com.deneme.java_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Kullanıcı giriş yaparken (Login) onu kullanıcı adından bulabilmemiz için:
    Optional<User> findByUsername(String username);
}