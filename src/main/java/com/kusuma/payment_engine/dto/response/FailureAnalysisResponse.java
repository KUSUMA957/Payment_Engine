package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record FailureAnalysisResponse(

		long totalTransactions,

		long failedTransactions,

		BigDecimal failureRate) {
}
