package com.kusuma.payment_engine.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.kusuma.payment_engine.dto.response.DailyTransactionSummaryResponse;
import com.kusuma.payment_engine.dto.response.DashboardSummaryResponse;
import com.kusuma.payment_engine.dto.response.DateRangeSummaryResponse;
import com.kusuma.payment_engine.dto.response.FailureAnalysisResponse;
import com.kusuma.payment_engine.dto.response.MonthlyTransactionTrendResponse;
import com.kusuma.payment_engine.dto.response.MonthlyVolumeTrendResponse;
import com.kusuma.payment_engine.dto.response.TopActiveUserResponse;
import com.kusuma.payment_engine.dto.response.TransactionVolumeByTypeResponse;

public interface AdminDashboardService {

	DashboardSummaryResponse getDashboardSummary();

	Map<String, Long> getTransactionStatusAnalytics();

	Map<String, Long> getTransactionTypeAnalytics();

	List<TopActiveUserResponse> getTopActiveUsers();

	DailyTransactionSummaryResponse getDailySummary();

	DateRangeSummaryResponse getRangeSummary(LocalDate fromDate, LocalDate toDate);

	List<MonthlyTransactionTrendResponse> getMonthlyTransactionTrend();

	List<MonthlyVolumeTrendResponse> getMonthlyVolumeTrend();

	FailureAnalysisResponse getFailureAnalysis();

	List<TransactionVolumeByTypeResponse> getTransactionVolumeByType();
}