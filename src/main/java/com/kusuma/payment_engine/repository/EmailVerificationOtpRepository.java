package com.kusuma.payment_engine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusuma.payment_engine.entity.EmailVerificationOtp;

public interface EmailVerificationOtpRepository extends JpaRepository<EmailVerificationOtp, Long> {

	Optional<EmailVerificationOtp> findTopByEmailOrderByCreatedAtDesc(String email);

}