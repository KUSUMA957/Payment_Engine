package com.kusuma.payment_engine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.request.ChangePasswordRequest;
import com.kusuma.payment_engine.dto.request.UpdateUserProfileRequest;
import com.kusuma.payment_engine.dto.response.UserProfileResponse;
import com.kusuma.payment_engine.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public UserProfileResponse getCurrentUser() {
		return userService.getCurrentUser();
	}

	@PutMapping("/me")
	public UserProfileResponse updateCurrentUser(@Valid @RequestBody UpdateUserProfileRequest request) {
		return userService.updateCurrentUser(request);
	}

	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
		userService.changePassword(request);
		return ResponseEntity.ok("Password changed successfully. Please login again.");
	}
}