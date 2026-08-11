package com.kusuma.payment_engine.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.request.ForgotPasswordRequest;
import com.kusuma.payment_engine.dto.request.LoginRequest;
import com.kusuma.payment_engine.dto.request.RegisterRequest;
import com.kusuma.payment_engine.dto.request.ResendOtpRequest;
import com.kusuma.payment_engine.dto.request.ResetPasswordRequest;
import com.kusuma.payment_engine.dto.request.VerifyOtpRequest;
import com.kusuma.payment_engine.dto.response.LoginResponse;
import com.kusuma.payment_engine.dto.response.RegisterResponse;
import com.kusuma.payment_engine.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
		RegisterResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/verify-email")
	public ResponseEntity<String> verifyEmail(@RequestBody @Valid VerifyOtpRequest request) {
		return ResponseEntity.ok(authService.verifyEmailOtp(request));
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<String> resendOtp(@RequestBody @Valid ResendOtpRequest request) {
		return ResponseEntity.ok(authService.resendOtp(request));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		return ResponseEntity.ok(authService.forgotPassword(request));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		return ResponseEntity.ok(authService.resetPassword(request));
	}
}
