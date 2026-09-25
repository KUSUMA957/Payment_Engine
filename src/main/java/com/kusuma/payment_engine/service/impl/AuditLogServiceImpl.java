package com.kusuma.payment_engine.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.dto.response.AuditLogResponse;
import com.kusuma.payment_engine.entity.AuditLog;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;
import com.kusuma.payment_engine.repository.AuditLogRepository;
import com.kusuma.payment_engine.service.AuditLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

	private final AuditLogRepository auditLogRepository;

	@Override
	@Transactional(
		    propagation = Propagation.REQUIRES_NEW
		)
	public void log(String performedBy, AuditAction action, AuditEntityType entityType, Long entityId,
			String description) {
		AuditLog auditLog = AuditLog.builder().performedBy(performedBy).action(action).entityType(entityType)
				.entityId(entityId).description(description).build();
		auditLogRepository.save(auditLog);
	}

	@Override
	public List<AuditLogResponse> getAllAuditLogs() {
		return auditLogRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse).toList();
	}

	private AuditLogResponse mapToResponse(AuditLog auditLog) {
		return AuditLogResponse.builder().id(auditLog.getId()).performedBy(auditLog.getPerformedBy())
				.action(auditLog.getAction()).entityType(auditLog.getEntityType()).entityId(auditLog.getEntityId())
				.description(auditLog.getDescription()).createdAt(auditLog.getCreatedAt()).build();
	}
}