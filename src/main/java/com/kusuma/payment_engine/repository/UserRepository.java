package com.kusuma.payment_engine.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kusuma.payment_engine.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
