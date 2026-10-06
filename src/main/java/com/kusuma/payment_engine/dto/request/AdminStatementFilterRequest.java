package com.kusuma.payment_engine.dto.request;

import java.time.LocalDate;

import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;

import lombok.Builder;

@Builder
public record AdminStatementFilterRequest(

		Long userId,

		LocalDate fromDate,

		LocalDate toDate,

		TransactionType transactionType,

		TransactionStatus transactionStatus,

		Integer page,

		Integer size) {

}
