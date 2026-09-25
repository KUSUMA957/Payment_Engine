package com.kusuma.payment_engine.dto.response;

import java.time.LocalDateTime;

import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;

import lombok.Builder;

@Builder
public record AuditLogResponse(

		Long id,

		String performedBy,

		AuditAction action,

		AuditEntityType entityType,

		Long entityId,

		String description,

		LocalDateTime createdAt) {
}