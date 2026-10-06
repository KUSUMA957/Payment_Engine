package com.kusuma.payment_engine.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.dto.response.DailyTransactionSummaryResponse;
import com.kusuma.payment_engine.dto.response.DashboardSummaryResponse;
import com.kusuma.payment_engine.dto.response.DateRangeSummaryResponse;
import com.kusuma.payment_engine.dto.response.FailureAnalysisResponse;
import com.kusuma.payment_engine.dto.response.MonthlyTransactionTrendResponse;
import com.kusuma.payment_engine.dto.response.MonthlyVolumeTrendResponse;
import com.kusuma.payment_engine.dto.response.TopActiveUserResponse;
import com.kusuma.payment_engine.dto.response.TransactionVolumeByTypeResponse;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.exception.InvalidTransactionException;
import com.kusuma.payment_engine.exception.UnauthorizedTransactionAccessException;
import com.kusuma.payment_engine.repository.AccountRepository;
import com.kusuma.payment_engine.repository.TransactionRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AdminDashboardService;
import com.kusuma.payment_engine.service.CurrentUserService;
import com.kusuma.payment_engine.util.AccountValidationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final CurrentUserService currentUserService;

	private User getValidatedAdmin() {
		User admin = currentUserService.getAuthenticatedUser();
		AccountValidationUtil.validateUserStatus(admin);
		if (admin.getRole() != Role.ADMIN) {
			throw new UnauthorizedTransactionAccessException("Admin access required");
		}
		return admin;
	}

	@Override
	public DashboardSummaryResponse getDashboardSummary() {
		getValidatedAdmin();
		return DashboardSummaryResponse.builder().totalUsers(userRepository.count())
				.totalAccounts(accountRepository.count()).totalTransactions(transactionRepository.count())
				.successfulTransactions(transactionRepository.countByStatus(TransactionStatus.SUCCESS))
				.failedTransactions(transactionRepository.countByStatus(TransactionStatus.FAILED))
				.pendingTransactions(transactionRepository.countByStatus(TransactionStatus.PENDING))
				.totalTransactionVolume(transactionRepository.getTotalTransactionVolume()).build();
	}

	@Override
	public Map<String, Long> getTransactionStatusAnalytics() {
		getValidatedAdmin();
		Map<String, Long> analytics = new HashMap<>();
		transactionRepository.getStatusAnalytics().forEach(row -> analytics.put(row[0].toString(), (Long) row[1]));
		return analytics;
	}

	@Override
	public Map<String, Long> getTransactionTypeAnalytics() {
		getValidatedAdmin();
		Map<String, Long> analytics = new HashMap<>();
		transactionRepository.getTypeAnalytics().forEach(row -> analytics.put(row[0].toString(), (Long) row[1]));
		return analytics;
	}

	@Override
	public List<TopActiveUserResponse> getTopActiveUsers() {
		getValidatedAdmin();
		List<Transaction> transactions = transactionRepository.findAll();
		Map<Long, Long> activityCount = new HashMap<>();
		Map<Long, User> users = new HashMap<>();
		for (Transaction transaction : transactions) {
			if (transaction.getSenderAccount() != null && transaction.getSenderAccount().getUser() != null) {
				User sender = transaction.getSenderAccount().getUser();
				users.put(sender.getId(), sender);
				activityCount.merge(sender.getId(), 1L, Long::sum);
			}
			if (transaction.getReceiverAccount() != null && transaction.getReceiverAccount().getUser() != null) {
				User receiver = transaction.getReceiverAccount().getUser();
				users.put(receiver.getId(), receiver);
				activityCount.merge(receiver.getId(), 1L, Long::sum);
			}
		}
		return activityCount.entrySet().stream().sorted(Map.Entry.<Long, Long>comparingByValue().reversed()).limit(5)
				.map(entry -> {
					User user = users.get(entry.getKey());
					return TopActiveUserResponse.builder().userId(user.getId()).userName(user.getFullName())
							.transactionCount(entry.getValue()).build();
				}).toList();
	}

	@Override
	public DailyTransactionSummaryResponse getDailySummary() {
		getValidatedAdmin();
		return DailyTransactionSummaryResponse.builder().date(LocalDate.now())
				.transactionsToday(transactionRepository.getTodayTransactionCount())
				.volumeToday(transactionRepository.getTodayTransactionVolume()).build();
	}

	@Override
	public DateRangeSummaryResponse getRangeSummary(LocalDate fromDate, LocalDate toDate) {
		getValidatedAdmin();
		if (fromDate.isAfter(toDate)) {
			throw new InvalidTransactionException("From date cannot be after To date");
		}
		LocalDateTime start = fromDate.atStartOfDay();
		LocalDateTime end = toDate.atTime(23, 59, 59);
		return DateRangeSummaryResponse.builder()
				.totalTransactions(transactionRepository.countTransactionsInRange(start, end))
				.successfulTransactions(
						transactionRepository.countTransactionsByStatusInRange(TransactionStatus.SUCCESS, start, end))
				.failedTransactions(
						transactionRepository.countTransactionsByStatusInRange(TransactionStatus.FAILED, start, end))
				.totalVolume(transactionRepository.getVolumeInRange(start, end)).build();
	}

	@Override
	public List<MonthlyTransactionTrendResponse> getMonthlyTransactionTrend() {
		getValidatedAdmin();
		return transactionRepository
				.getMonthlyTransactionTrend().stream().map(row -> MonthlyTransactionTrendResponse.builder()
						.month(String.valueOf(row[0])).transactionCount(((Number) row[1]).longValue()).build())
				.toList();
	}

	@Override
	public List<MonthlyVolumeTrendResponse> getMonthlyVolumeTrend() {
		getValidatedAdmin();
		return transactionRepository.getMonthlyVolumeTrend().stream().map(row -> MonthlyVolumeTrendResponse.builder()
				.month(String.valueOf(row[0])).volume((BigDecimal) row[1]).build()).toList();
	}

	@Override
	public FailureAnalysisResponse getFailureAnalysis() {
		getValidatedAdmin();
		long total = transactionRepository.count();
		long failed = transactionRepository.countByStatus(TransactionStatus.FAILED);
		BigDecimal failureRate = total == 0 ? BigDecimal.ZERO
				: BigDecimal.valueOf(failed * 100.0).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
		return FailureAnalysisResponse.builder().totalTransactions(total).failedTransactions(failed)
				.failureRate(failureRate).build();
	}

	@Override
	public List<TransactionVolumeByTypeResponse> getTransactionVolumeByType() {
		getValidatedAdmin();
		return transactionRepository.getTransactionVolumeByType().stream().map(row -> TransactionVolumeByTypeResponse
				.builder().transactionType(row[0].toString()).volume((BigDecimal) row[1]).build()).toList();
	}

}