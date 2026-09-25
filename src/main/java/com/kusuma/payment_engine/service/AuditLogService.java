package com.kusuma.payment_engine.service;

import java.util.List;
import com.kusuma.payment_engine.dto.response.AuditLogResponse;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;

public interface AuditLogService {
	void log(String performedBy, AuditAction action, AuditEntityType entityType, Long entityId, String description);
	List<AuditLogResponse> getAllAuditLogs();
}