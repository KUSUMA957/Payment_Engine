package com.kusuma.payment_engine.dto.response;

import lombok.Builder;

@Builder
public record FraudAlertActionResponse(String message) {
}