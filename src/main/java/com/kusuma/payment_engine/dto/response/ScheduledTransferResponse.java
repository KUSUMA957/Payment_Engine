package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kusuma.payment_engine.enums.ScheduledTransferStatus;
import com.kusuma.payment_engine.enums.TransferFrequency;

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

		LocalDate nextExecutionDate,

		LocalDate endDate,

		LocalDateTime executedAt,

		String failureReason,

		TransferFrequency frequency,

		ScheduledTransferStatus status) {

}