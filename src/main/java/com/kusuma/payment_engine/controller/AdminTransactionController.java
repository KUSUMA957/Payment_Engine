package com.kusuma.payment_engine.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.request.AdminStatementFilterRequest;
import com.kusuma.payment_engine.dto.response.TransactionResponse;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;
import com.kusuma.payment_engine.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {
	
	private final TransactionService transactionService;
	
	@GetMapping("/users/{userId}")
	public ResponseEntity<Page<TransactionResponse>> getTransactionsByUser(
			@PathVariable("userId") Long userId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {
		return ResponseEntity.ok(transactionService.getTransactionsByUser(userId, page, size));
	}

	@GetMapping("/{reference}")
	public ResponseEntity<TransactionResponse> getTransactionByReference(@PathVariable("reference") String reference) {
		return ResponseEntity.ok(transactionService.getAdminTransactionByReference(reference));
	}

	@GetMapping("/statement")
	public ResponseEntity<Page<TransactionResponse>> getAdminStatement(
			@RequestParam(name = "userId", required = false) Long userId,
			@RequestParam(name = "fromDate", required = false) LocalDate fromDate,
			@RequestParam(name = "toDate", required = false) LocalDate toDate,
			@RequestParam(name = "transactionType", required = false) TransactionType transactionType,
			@RequestParam(name = "transactionStatus", required = false) TransactionStatus transactionStatus,
			@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "size", defaultValue = "20") Integer size) {
		AdminStatementFilterRequest request = AdminStatementFilterRequest.builder().userId(userId).fromDate(fromDate)
				.toDate(toDate).transactionType(transactionType).transactionStatus(transactionStatus).page(page)
				.size(size).build();
		return ResponseEntity.ok(transactionService.getAdminStatement(request));
	}

}
