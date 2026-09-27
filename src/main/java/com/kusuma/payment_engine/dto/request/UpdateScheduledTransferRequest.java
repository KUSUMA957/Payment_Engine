package com.kusuma.payment_engine.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.kusuma.payment_engine.enums.TransferFrequency;

public record UpdateScheduledTransferRequest(

		BigDecimal amount,

		String description,

		TransferFrequency frequency,

		LocalDate endDate) {
}
