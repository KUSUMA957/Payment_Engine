package com.kusuma.payment_engine.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kusuma.payment_engine.enums.TransferFrequency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateScheduledTransferRequest(

		@NotNull(message = "Beneficiary id is required") Long beneficiaryId,

		@NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,

		String description,

		@NotNull(message = "Schedule date is required") @JsonFormat(pattern = "dd-MM-yyyy") LocalDate scheduledAt,

		@NotNull(message = "Frequency is required") TransferFrequency frequency,

		@JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {

}