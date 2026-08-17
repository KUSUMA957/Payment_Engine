package com.kusuma.payment_engine.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.response.AdminUserResponse;
import com.kusuma.payment_engine.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@GetMapping("/users")
	@PreAuthorize("hasRole('ADMIN')")
	public List<AdminUserResponse> getAllUsers() {
		return adminService.getAllUsers();
	}

	@GetMapping("/users/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public AdminUserResponse getUserById(@PathVariable("id") Long id) {
		return adminService.getUserById(id);
	}

	@PutMapping("/users/{id}/lock")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> lockUser(@PathVariable("id") Long id) {
		adminService.lockUser(id);
		return ResponseEntity.ok("User locked successfully");
	}

	@PutMapping("/users/{id}/unlock")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> unlockUser(@PathVariable("id") Long id) {
		adminService.unlockUser(id);
		return ResponseEntity.ok("User unlocked successfully");
	}

	@PutMapping("/users/{id}/disable")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> disableUser(@PathVariable("id") Long id) {
		adminService.disableUser(id);
		return ResponseEntity.ok("User disabled successfully");
	}

	@PutMapping("/users/{id}/enable")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> enableUser(@PathVariable("id") Long id) {
		adminService.enableUser(id);
		return ResponseEntity.ok("User enabled successfully");
	}

}