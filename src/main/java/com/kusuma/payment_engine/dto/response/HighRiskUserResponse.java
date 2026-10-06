package com.kusuma.payment_engine.dto.response;

import com.kusuma.payment_engine.enums.RiskLevel;

import lombok.Builder;

@Builder
public record HighRiskUserResponse(

		Long userId,

		String email,

		Integer riskScore,

		RiskLevel riskLevel) {

}