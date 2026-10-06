package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record TransactionVolumeByTypeResponse(

		String transactionType,

		BigDecimal volume) {
}
