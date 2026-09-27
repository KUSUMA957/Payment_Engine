package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kusuma.payment_engine.enums.ScheduledTransferStatus;

import lombok.Builder;

@Builder
public record ScheduledTransferResponse(

		Long id,

		Long beneficiaryId,

		String beneficiaryNickname,

		String beneficiaryAccountNumber,

		BigDecimal amount,

		String description,

		LocalDate scheduledAt,

		LocalDateTime executedAt,

		String failureReason,

		ScheduledTransferStatus status) {
}