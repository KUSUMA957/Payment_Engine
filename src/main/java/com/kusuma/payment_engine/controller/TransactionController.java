package com.kusuma.payment_engine.controller;

import java.time.LocalDate;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.kusuma.payment_engine.dto.request.BeneficiaryTransferRequest;
import com.kusuma.payment_engine.dto.request.StatementFilterRequest;
import com.kusuma.payment_engine.dto.request.TransactionAmountRequest;
import com.kusuma.payment_engine.dto.request.TransferRequest;
import com.kusuma.payment_engine.dto.response.TransactionResponse;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;
import com.kusuma.payment_engine.service.PdfStatementService;
import com.kusuma.payment_engine.service.TransactionService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;
	private final PdfStatementService pdfStatementService;

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

	@PostMapping("/beneficiary-transfer")
	public ResponseEntity<TransactionResponse> transferToBeneficiary(
			@Valid @RequestBody BeneficiaryTransferRequest request) {
		return ResponseEntity.ok(transactionService.transferToBeneficiary(request));
	}

	@GetMapping("/mini-statement")
	public ResponseEntity<List<TransactionResponse>> getMiniStatement() {
		return ResponseEntity.ok(transactionService.getMiniStatement());
	}

	@GetMapping("/statement")
	public ResponseEntity<Page<TransactionResponse>> getStatement(
			@Parameter(description = "Statement start date") @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
			@Parameter(description = "Statement end date") @RequestParam(name = "toDate", required = false) LocalDate toDate,
			@RequestParam(name = "transactionType", required = false) TransactionType transactionType,
			@RequestParam(name = "transactionStatus", required = false) TransactionStatus transactionStatus,
			@Parameter(description = "Statement Page Number") @RequestParam(name = "PageNumber", defaultValue = "0") Integer page,
			@Parameter(description = "Specify number of records") @RequestParam(name = "Num_Of_records", defaultValue = "20") Integer size) {
		StatementFilterRequest request = StatementFilterRequest.builder().fromDate(fromDate).toDate(toDate)
				.transactionType(transactionType).transactionStatus(transactionStatus).page(page).size(size).build();
		return ResponseEntity.ok(transactionService.getStatement(request));
	}

	@GetMapping("/statement/export/pdf")
	public ResponseEntity<byte[]> downloadStatementPdf(
			@RequestParam(name = "fromDate", required = false) LocalDate fromDate,
			@RequestParam(name = "toDate", required = false) LocalDate toDate) {
		System.out.println("Controller FromDate = " + fromDate);
		System.out.println("Controller ToDate   = " + toDate);
		byte[] pdf = pdfStatementService.generateStatementPdf(fromDate, toDate);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statement.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdf);
	}
}
