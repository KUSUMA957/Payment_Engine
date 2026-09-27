package com.kusuma.payment_engine.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kusuma.payment_engine.enums.ScheduledTransferStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scheduled_transfers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransfer extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beneficiary_id", nullable = false)
	private Beneficiary beneficiary;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(length = 255)
	private String description;

	@Column(nullable = false)
	private LocalDate scheduledAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ScheduledTransferStatus status;

	@Column(length = 500)
	private String failureReason;

	private LocalDateTime executedAt;
}