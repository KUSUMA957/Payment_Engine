package com.kusuma.payment_engine.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.request.TransactionAmountRequest;
import com.kusuma.payment_engine.dto.request.TransferRequest;
import com.kusuma.payment_engine.dto.response.TransactionResponse;
import com.kusuma.payment_engine.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@PostMapping("/transfer")
	public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
		return ResponseEntity.ok(transactionService.transfer(request));
	}

	@GetMapping("/history")
	public ResponseEntity<List<TransactionResponse>> getMyTransactions() {
		return ResponseEntity.ok(transactionService.getMyTransactions());
	}

	@GetMapping("/{reference}")
	public ResponseEntity<TransactionResponse> getTransaction(@PathVariable("reference") String reference) {
		return ResponseEntity.ok(transactionService.getTransaction(reference));
	}

	@PostMapping("/deposit")
	public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody TransactionAmountRequest request) {
		return ResponseEntity.ok(transactionService.deposit(request));
	}

	@PostMapping("/withdraw")
	public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody TransactionAmountRequest request) {
		return ResponseEntity.ok(transactionService.withdraw(request));
	}
}
