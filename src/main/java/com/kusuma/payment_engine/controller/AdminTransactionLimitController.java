package com.kusuma.payment_engine.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.request.UpdateTransactionLimitRequest;
import com.kusuma.payment_engine.dto.response.TransactionLimitResponse;
import com.kusuma.payment_engine.enums.TransactionLimitType;
import com.kusuma.payment_engine.service.TransactionLimitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/transaction-limits")
@RequiredArgsConstructor
public class AdminTransactionLimitController {

	private final TransactionLimitService transactionLimitService;

	@GetMapping
	public ResponseEntity<List<TransactionLimitResponse>> getAllLimits() {
		return ResponseEntity.ok(transactionLimitService.getAllLimits());
	}

	@PutMapping("/{limitType}")
	public ResponseEntity<TransactionLimitResponse> updateLimit(@PathVariable("limitType") TransactionLimitType limitType,
			@RequestBody UpdateTransactionLimitRequest request) {
		return ResponseEntity.ok(transactionLimitService.updateLimit(limitType, request));
	}

	@PatchMapping("/{limitType}/toggle")
	public ResponseEntity<Void> toggleLimit(@PathVariable("limitType") TransactionLimitType limitType) {
		transactionLimitService.toggleLimit(limitType);
		return ResponseEntity.noContent().build();
	}
}