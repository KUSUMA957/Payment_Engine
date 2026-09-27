package com.kusuma.payment_engine.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.request.CreateScheduledTransferRequest;
import com.kusuma.payment_engine.dto.response.ScheduledTransferResponse;
import com.kusuma.payment_engine.service.ScheduledTransferService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/scheduled-transfers")
@RequiredArgsConstructor
public class ScheduledTransferController {

	private final ScheduledTransferService scheduledTransferService;

	@PostMapping
	public ResponseEntity<ScheduledTransferResponse> scheduleTransfer(
			@Valid @RequestBody CreateScheduledTransferRequest request) {
		return ResponseEntity.ok(scheduledTransferService.scheduleTransfer(request));
	}

	@GetMapping
	public ResponseEntity<List<ScheduledTransferResponse>> getMyScheduledTransfers() {
		return ResponseEntity.ok(scheduledTransferService.getMyScheduledTransfers());
	}

	@GetMapping("/{transferId}")
	public ResponseEntity<ScheduledTransferResponse> getScheduledTransfer(@PathVariable("transferId") Long transferId) {
		return ResponseEntity.ok(scheduledTransferService.getScheduledTransfer(transferId));
	}

	@DeleteMapping("/{transferId}")
	public ResponseEntity<String> cancelTransfer(@PathVariable("transferId") Long transferId) {
		scheduledTransferService.cancelScheduledTransfer(transferId);
		return ResponseEntity.ok("Scheduled transfer cancelled successfully");
	}
}
