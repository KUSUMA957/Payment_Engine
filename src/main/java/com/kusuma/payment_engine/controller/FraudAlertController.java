package com.kusuma.payment_engine.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.entity.FraudAlert;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.exception.UnauthorizedTransactionAccessException;
import com.kusuma.payment_engine.repository.FraudAlertRepository;
import com.kusuma.payment_engine.service.CurrentUserService;
import com.kusuma.payment_engine.service.FraudDetectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/fraud-alerts")
@RequiredArgsConstructor
public class FraudAlertController {

	private final CurrentUserService currentUserService;
	private final FraudAlertRepository fraudAlertRepository;
	
	@GetMapping
	public ResponseEntity<List<FraudAlert>> getFraudAlerts() {
		getValidatedAdmin();
		return ResponseEntity.ok(fraudAlertRepository.findByResolvedFalseOrderByCreatedAtDesc());
	}

	private User getValidatedAdmin() {
		User admin = currentUserService.getAuthenticatedUser();
		if (admin.getRole() != Role.ADMIN) {
			throw new UnauthorizedTransactionAccessException("Admin access required");
		}
		return admin;
	}
}
