package com.kusuma.payment_engine.dto.request;

import java.math.BigDecimal;

public record UpdateTransactionLimitRequest(

		BigDecimal limitAmount) {

}
