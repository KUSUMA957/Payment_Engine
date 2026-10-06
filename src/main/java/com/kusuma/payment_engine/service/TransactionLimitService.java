package com.kusuma.payment_engine.service;

import java.math.BigDecimal;
import java.util.List;

import com.kusuma.payment_engine.dto.request.UpdateTransactionLimitRequest;
import com.kusuma.payment_engine.dto.response.TransactionLimitResponse;
import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.enums.TransactionLimitType;

public interface TransactionLimitService {

	void validateTransferLimits(Account account, BigDecimal amount);

	void validateBeneficiaryTransferLimits(Account account, BigDecimal amount);

	void validateWithdrawalLimit(BigDecimal amount);

	void validateScheduledTransferCreation(BigDecimal amount);

	void validateScheduledTransferExecution(Account account, BigDecimal amount);

	List<TransactionLimitResponse> getAllLimits();

	TransactionLimitResponse updateLimit(TransactionLimitType limitType, UpdateTransactionLimitRequest request);

	void toggleLimit(TransactionLimitType limitType);
}