package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record DateRangeSummaryResponse(

		long totalTransactions,

		long successfulTransactions,

		long failedTransactions,

		BigDecimal totalVolume) {
}