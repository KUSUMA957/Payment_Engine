package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;

import com.kusuma.payment_engine.enums.TransactionLimitType;

import lombok.Builder;

@Builder
public record TransactionLimitResponse(

		TransactionLimitType limitType,

		BigDecimal limitAmount,

		boolean active) {
}