package com.kusuma.payment_engine.dto.response;

import lombok.Builder;

@Builder
public record TopActiveUserResponse(

		Long userId,

		String userName,

		Long transactionCount) {

}