package com.kusuma.payment_engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBeneficiaryRequest(@NotBlank @Size(min = 2, max = 50) String nickname) {
}