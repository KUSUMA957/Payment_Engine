package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;

@Builder
public record DailyTransactionSummaryResponse(

		LocalDate date,

		long transactionsToday,

		BigDecimal volumeToday) {

}