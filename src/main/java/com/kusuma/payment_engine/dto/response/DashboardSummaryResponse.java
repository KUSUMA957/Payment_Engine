package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record DashboardSummaryResponse(

		long totalUsers,

		long totalAccounts,

		long totalTransactions,

		long successfulTransactions,

		long failedTransactions,

		long pendingTransactions,

		BigDecimal totalTransactionVolume) {

}