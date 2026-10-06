package com.kusuma.payment_engine.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.FraudAlert;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.FraudType;
import com.kusuma.payment_engine.enums.RiskLevel;
import com.kusuma.payment_engine.enums.TransactionLimitType;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;
import com.kusuma.payment_engine.repository.AccountRepository;
import com.kusuma.payment_engine.repository.FraudAlertRepository;
import com.kusuma.payment_engine.repository.TransactionLimitRepository;
import com.kusuma.payment_engine.repository.TransactionRepository;
import com.kusuma.payment_engine.service.FraudDetectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService {

	private final TransactionRepository transactionRepository;
	private final FraudAlertRepository fraudAlertRepository;
	private final AccountRepository accountRepository;
	private final TransactionLimitRepository transactionLimitRepository;

	private static final BigDecimal HIGH_VALUE_THRESHOLD = BigDecimal.valueOf(50000);

	@Override
	public void analyzeRecentTransactions() {
		List<Account> accounts = accountRepository.findAll();
		for (Account account : accounts) {
			detectTransferVelocity(account);
			detectSpendingSpike(account);
		}
	}

	private void detectTransferVelocity(Account account) {
		long transferCount = transactionRepository.countTransfersSince(account, LocalDateTime.now().minusMinutes(10));
		if (transferCount >= 5) {
			createAlert(account.getUser(), null, FraudType.TRANSFER_VELOCITY, RiskLevel.HIGH,
					"More than 5 transfers within 10 minutes");
		}
	}

	private void detectSpendingSpike(Account account) {
		BigDecimal averageAmount = transactionRepository.getAverageTransferAmount(account);
		BigDecimal todayAmount = transactionRepository.getTodayTotalTransferAmount(account);
		if (averageAmount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		if (todayAmount.compareTo(averageAmount.multiply(BigDecimal.valueOf(20))) > 0) {
			createAlert(account.getUser(), null, FraudType.SPENDING_SPIKE, RiskLevel.CRITICAL,
					"Today's expenditure exceeds historical behaviour");
		}
	}

	private void createAlert(User user, Transaction transaction, FraudType fraudType, RiskLevel riskLevel,
			String reason) {
		if (transaction != null && fraudAlertRepository.existsByTransactionAndFraudType(transaction, fraudType)) {
			return;
		}
		if (fraudAlertRepository.existsByUserAndFraudTypeAndResolvedFalse(user, fraudType)) {
			return;
		}
		FraudAlert alert = FraudAlert.builder().user(user).transaction(transaction).fraudType(fraudType)
				.riskLevel(riskLevel).riskScore(getRiskScore(fraudType)).reason(reason).resolved(false).build();
		fraudAlertRepository.save(alert);
	}

	@Override
	public void evaluateTransaction(Transaction transaction) {
		if (transaction == null) {
			return;
		}
		if (transaction.getStatus() != TransactionStatus.SUCCESS) {
			return;
		}
		if (transaction.getTransactionType() != TransactionType.TRANSFER) {
			return;
		}
		Account sender = transaction.getSenderAccount();
		if (sender == null) {
			return;
		}
		detectTransferVelocity(sender, transaction);
		detectHighValueActivity(transaction);
		detectNearLimitActivity(transaction);
	}

	private void detectTransferVelocity(Account account, Transaction transaction) {
		long transferCount = transactionRepository.countTransfersSince(account, LocalDateTime.now().minusMinutes(10));
		if (transferCount >= 5) {
			createAlert(account.getUser(), transaction, FraudType.TRANSFER_VELOCITY, RiskLevel.HIGH,
					"More than 5 successful transfers within 10 minutes");
		}
	}

	private void detectHighValueActivity(Transaction transaction) {
		if (transaction.getAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0) {
			createAlert(transaction.getSenderAccount().getUser(), transaction, FraudType.HIGH_VALUE_ACTIVITY,
					RiskLevel.HIGH, "High value transaction detected");
		}
	}

	private void detectNearLimitActivity(Transaction transaction) {
		transactionLimitRepository.findByLimitTypeAndActiveTrue(TransactionLimitType.PER_TRANSFER).ifPresent(limit -> {
			BigDecimal threshold = limit.getLimitAmount().multiply(BigDecimal.valueOf(0.90));
			if (transaction.getAmount().compareTo(threshold) >= 0) {
				createAlert(transaction.getSenderAccount().getUser(), transaction, FraudType.LIMIT_ABUSE,
						RiskLevel.MEDIUM, "Transaction amount is above 90% of configured transfer limit");
			}
		});
	}

	private int getRiskScore(FraudType fraudType) {
		return switch (fraudType) {
		case LIMIT_ABUSE -> 20;
		case HIGH_VALUE_ACTIVITY -> 30;
		case TRANSFER_VELOCITY -> 40;
		case SPENDING_SPIKE -> 50;
		};
	}

}
