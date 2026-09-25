package com.kusuma.payment_engine.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kusuma.payment_engine.dto.response.AuditLogResponse;
import com.kusuma.payment_engine.service.AuditLogService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

	private final AuditLogService auditLogService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<AuditLogResponse> getAuditLogs() {
		return auditLogService.getAllAuditLogs();
	}
}