package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;

import com.kusuma.payment_engine.enums.AccountStatus;
import com.kusuma.payment_engine.enums.CurrencyCode;

import lombok.Builder;

@Builder
public record AccountResponse(

		String accountNumber,

		BigDecimal balance,

		CurrencyCode currency,

		AccountStatus status) {
}