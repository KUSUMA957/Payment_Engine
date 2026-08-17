package com.kusuma.payment_engine.entity;

import java.time.LocalDateTime;

import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(unique = true, length = 10)
    private String phoneNumber;
    
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private LocalDateTime lastLoginAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    private LocalDateTime accountLockedUntil;
    
    @Column(name = "last_password_changed_at")
    private LocalDateTime lastPasswordChangedAt;
}