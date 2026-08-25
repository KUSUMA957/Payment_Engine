package com.kusuma.payment_engine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.response.AccountResponse;
import com.kusuma.payment_engine.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping
	public ResponseEntity<AccountResponse> createAccount() {
		return ResponseEntity.ok(accountService.createAccount());
	}

	@GetMapping("/me")
	public ResponseEntity<AccountResponse> getMyAccount() {
		return ResponseEntity.ok(accountService.getMyAccount());
	}

	@PutMapping("/{id}/freeze")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> freezeAccount(@PathVariable("id") Long id) {
		accountService.freezeAccount(id);
		return ResponseEntity.ok("Account frozen successfully");
	}

	@PutMapping("/{id}/unfreeze")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> unfreezeAccount(@PathVariable("id") Long id) {
		accountService.unfreezeAccount(id);
		return ResponseEntity.ok("Account unfrozen successfully");
	}

	@PutMapping("/{id}/close")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> closeAccount(@PathVariable("id") Long id) {
		accountService.closeAccount(id);
		return ResponseEntity.ok("Account closed successfully");
	}
}