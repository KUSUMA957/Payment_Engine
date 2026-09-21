package com.kusuma.payment_engine.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.kusuma.payment_engine.enums.CurrencyCode;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "transactions", indexes = {
		@Index(name = "idx_transaction_reference", columnList = "transactionReference") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {

	@Column(name = "transaction_reference", nullable = false, unique = true, length = 30)
	private String transactionReference;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_account_id")
	private Account senderAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_account_id")
	private Account receiverAccount;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CurrencyCode currency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType transactionType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionStatus status;

	@Column(length = 255)
	private String description;

	private LocalDateTime processedAt;
}