package com.kusuma.payment_engine.entity;

import jakarta.persistence.*;

import lombok.*;

import com.kusuma.payment_engine.enums.FraudType;
import com.kusuma.payment_engine.enums.RiskLevel;

@Entity
@Table(name = "fraud_alerts", indexes = { @Index(name = "idx_fraud_user", columnList = "user_id"),
		@Index(name = "idx_fraud_resolved", columnList = "resolved") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	private Transaction transaction;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FraudType fraudType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RiskLevel riskLevel;

	@Column(nullable = false, length = 500)
	private String reason;

	@Column(nullable = false)
	private boolean resolved;
}