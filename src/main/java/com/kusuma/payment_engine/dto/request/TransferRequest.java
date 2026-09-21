package com.kusuma.payment_engine.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TransferRequest(

		@NotBlank String receiverAccountNumber,

		@NotNull @DecimalMin("0.01") BigDecimal amount,

		@Size(max = 255) String description) {
}