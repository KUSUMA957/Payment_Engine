package com.kusuma.payment_engine.entity;

import java.math.BigDecimal;

import com.kusuma.payment_engine.enums.TransactionLimitType;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "transaction_limits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimit extends BaseEntity {
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true)
	private TransactionLimitType limitType;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal limitAmount;

	@Column(nullable = false)
	private boolean active = true;
}