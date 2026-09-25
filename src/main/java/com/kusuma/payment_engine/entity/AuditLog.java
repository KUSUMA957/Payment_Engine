package com.kusuma.payment_engine.entity;

import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs", indexes = { @Index(name = "idx_audit_action", columnList = "action"),
		@Index(name = "idx_audit_entity_type", columnList = "entityType"),
		@Index(name = "idx_audit_performed_by", columnList = "performedBy") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

	@Column(nullable = false, length = 150)
	private String performedBy;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private AuditAction action;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private AuditEntityType entityType;

	private Long entityId;

	@Column(nullable = false, length = 500)
	private String description;
}