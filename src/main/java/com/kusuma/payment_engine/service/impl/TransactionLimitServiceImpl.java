package com.kusuma.payment_engine.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.dto.request.UpdateTransactionLimitRequest;
import com.kusuma.payment_engine.dto.response.TransactionLimitResponse;
import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.TransactionLimit;
import com.kusuma.payment_engine.enums.TransactionLimitType;
import com.kusuma.payment_engine.exception.InvalidTransactionException;
import com.kusuma.payment_engine.exception.TransactionLimitConfigurationException;
import com.kusuma.payment_engine.exception.TransactionLimitExceededException;
import com.kusuma.payment_engine.repository.TransactionLimitRepository;
import com.kusuma.payment_engine.repository.TransactionRepository;
import com.kusuma.payment_engine.service.TransactionLimitService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionLimitServiceImpl implements TransactionLimitService {

	private final TransactionLimitRepository transactionLimitRepository;
	private final TransactionRepository transactionRepository;

	@Override
	public void validateTransferLimits(Account account, BigDecimal amount) {
		validatePerTransferLimit(amount);
		validateDailyTransferLimit(account, amount);
		validateMonthlyTransferLimit(account, amount);
	}

	@Override
	public void validateBeneficiaryTransferLimits(Account account, BigDecimal amount) {
		validatePerTransferLimit(amount);
		validateBeneficiaryTransferLimit(amount);
		validateDailyTransferLimit(account, amount);
		validateMonthlyTransferLimit(account, amount);
	}

	@Override
	public void validateWithdrawalLimit(BigDecimal amount) {
		Optional<TransactionLimit> optionalLimit = getActiveLimit(TransactionLimitType.PER_WITHDRAWAL);
		if (optionalLimit.isEmpty()) {
			return;
		}
		TransactionLimit limit = optionalLimit.get();
		if (amount.compareTo(limit.getLimitAmount()) > 0) {
			throw new TransactionLimitExceededException("Withdrawal limit exceeded");
		}
	}

	@Override
	public void validateScheduledTransferCreation(BigDecimal amount) {
		validatePerTransferLimit(amount);
		validateBeneficiaryTransferLimit(amount);
	}

	@Override
	public void validateScheduledTransferExecution(Account account, BigDecimal amount) {
		validatePerTransferLimit(amount);
		validateDailyTransferLimit(account, amount);
		validateMonthlyTransferLimit(account, amount);
	}

	private void validatePerTransferLimit(BigDecimal amount) {
		Optional<TransactionLimit> optionalLimit = getActiveLimit(TransactionLimitType.PER_TRANSFER);
		if (optionalLimit.isEmpty()) {
			return;
		}
		TransactionLimit limit = optionalLimit.get();
		if (amount.compareTo(limit.getLimitAmount()) > 0) {
			throw new TransactionLimitExceededException("Per transfer limit exceeded");
		}
	}

	private void validateBeneficiaryTransferLimit(BigDecimal amount) {
		Optional<TransactionLimit> optionalLimit = getActiveLimit(TransactionLimitType.BENEFICIARY_TRANSFER);
		if (optionalLimit.isEmpty()) {
			return;
		}
		TransactionLimit limit = optionalLimit.get();
		if (amount.compareTo(limit.getLimitAmount()) > 0) {
			throw new TransactionLimitExceededException("Beneficiary transfer limit exceeded");
		}
	}

	private void validateDailyTransferLimit(Account account, BigDecimal amount) {
		Optional<TransactionLimit> optionalLimit = getActiveLimit(TransactionLimitType.DAILY_TRANSFER);
		if (optionalLimit.isEmpty()) {
			return;
		}
		TransactionLimit limit = optionalLimit.get();
		BigDecimal todayAmount = transactionRepository.getTodayTransferAmount(account);
		if (todayAmount.add(amount).compareTo(limit.getLimitAmount()) > 0) {
			throw new TransactionLimitExceededException("Daily transfer limit exceeded");
		}
	}

	private void validateMonthlyTransferLimit(Account account, BigDecimal amount) {
		Optional<TransactionLimit> optionalLimit = getActiveLimit(TransactionLimitType.MONTHLY_TRANSFER);
		if (optionalLimit.isEmpty()) {
			return;
		}
		TransactionLimit limit = optionalLimit.get();
		BigDecimal monthAmount = transactionRepository.getMonthlyTransferAmount(account);
		if (monthAmount.add(amount).compareTo(limit.getLimitAmount()) > 0) {
			throw new TransactionLimitExceededException("Monthly transfer limit exceeded");
		}
	}

	private Optional<TransactionLimit> getActiveLimit(TransactionLimitType type) {
		return transactionLimitRepository.findByLimitTypeAndActiveTrue(type);
	}

	@Override
	public List<TransactionLimitResponse> getAllLimits() {
		return transactionLimitRepository.findAll().stream().map(limit -> TransactionLimitResponse.builder()
				.limitType(limit.getLimitType()).limitAmount(limit.getLimitAmount()).active(limit.isActive()).build())
				.toList();
	}

	@Override
	public TransactionLimitResponse updateLimit(TransactionLimitType limitType, UpdateTransactionLimitRequest request) {
		if (request.limitAmount() == null || request.limitAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Limit amount must be greater than zero");
		}
		TransactionLimit limit = transactionLimitRepository.findByLimitType(limitType)
				.orElseThrow(() -> new TransactionLimitConfigurationException("Limit not found: " + limitType));
		validateLimitHierarchy(limitType, request.limitAmount());
		limit.setLimitAmount(request.limitAmount());
		limit = transactionLimitRepository.save(limit);
		return TransactionLimitResponse.builder().limitType(limit.getLimitType()).limitAmount(limit.getLimitAmount())
				.active(limit.isActive()).build();
	}

	@Override
	public void toggleLimit(TransactionLimitType limitType) {
		TransactionLimit limit = transactionLimitRepository.findByLimitType(limitType)
				.orElseThrow(() -> new TransactionLimitConfigurationException("Limit not found: " + limitType));
		limit.setActive(!limit.isActive());
		transactionLimitRepository.save(limit);
	}

	private void validateLimitHierarchy(TransactionLimitType limitType, BigDecimal newValue) {
		BigDecimal perTransfer = getLimitAmount(TransactionLimitType.PER_TRANSFER);
		BigDecimal dailyTransfer = getLimitAmount(TransactionLimitType.DAILY_TRANSFER);
		BigDecimal monthlyTransfer = getLimitAmount(TransactionLimitType.MONTHLY_TRANSFER);
		if (limitType == TransactionLimitType.PER_TRANSFER) {
			perTransfer = newValue;
		}
		if (limitType == TransactionLimitType.DAILY_TRANSFER) {
			dailyTransfer = newValue;
		}
		if (limitType == TransactionLimitType.MONTHLY_TRANSFER) {
			monthlyTransfer = newValue;
		}
		if (dailyTransfer.compareTo(perTransfer) < 0) {
			throw new InvalidTransactionException("Daily transfer limit cannot be less than Per Transfer limit");
		}
		if (monthlyTransfer.compareTo(dailyTransfer) < 0) {
			throw new InvalidTransactionException("Monthly transfer limit cannot be less than Daily Transfer limit");
		}
	}

	private BigDecimal getLimitAmount(TransactionLimitType type) {
		return transactionLimitRepository.findByLimitType(type)
				.orElseThrow(() -> new TransactionLimitConfigurationException("Limit not found")).getLimitAmount();
	}
}
