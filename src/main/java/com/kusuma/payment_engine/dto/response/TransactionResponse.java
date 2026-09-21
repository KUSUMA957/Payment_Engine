package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.kusuma.payment_engine.enums.CurrencyCode;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;

import lombok.Builder;

@Builder
public record TransactionResponse(

		String transactionReference,

		String senderAccountNumber,

		String receiverAccountNumber,

		BigDecimal amount,

		CurrencyCode currency,

		TransactionType transactionType,

		TransactionStatus status,

		String description,

		LocalDateTime processedAt) {
}