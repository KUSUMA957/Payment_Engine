package com.kusuma.payment_engine.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email_verification_otps")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationOtp extends BaseEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime lastSentAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean used = false;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer failedAttempts = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean locked = false;
}