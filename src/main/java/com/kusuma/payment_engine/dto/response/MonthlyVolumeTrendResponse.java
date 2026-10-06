package com.kusuma.payment_engine.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record MonthlyVolumeTrendResponse(

		String month,

		BigDecimal volume) {
}