package com.kusuma.payment_engine.dto.response;

import lombok.Builder;

@Builder
public record BeneficiaryResponse(Long id, String nickname, String accountNumber, String beneficiaryName) {
}