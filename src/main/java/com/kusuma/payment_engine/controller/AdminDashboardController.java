package com.kusuma.payment_engine.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.response.DateRangeSummaryResponse;
import com.kusuma.payment_engine.dto.response.DailyTransactionSummaryResponse;
import com.kusuma.payment_engine.dto.response.DashboardSummaryResponse;
import com.kusuma.payment_engine.dto.response.FailureAnalysisResponse;
import com.kusuma.payment_engine.dto.response.MonthlyTransactionTrendResponse;
import com.kusuma.payment_engine.dto.response.MonthlyVolumeTrendResponse;
import com.kusuma.payment_engine.dto.response.TopActiveUserResponse;
import com.kusuma.payment_engine.dto.response.TransactionVolumeByTypeResponse;
import com.kusuma.payment_engine.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

	private final AdminDashboardService adminDashboardService;

	/**
	 * Overall Dashboard Summary
	 */
	@GetMapping("/summary")
	public ResponseEntity<DashboardSummaryResponse> getSummary() {
		return ResponseEntity.ok(adminDashboardService.getDashboardSummary());
	}

	/**
	 * Transaction Status Analytics
	 */
	@GetMapping("/transaction-status")
	public ResponseEntity<Map<String, Long>> getStatusAnalytics() {
		return ResponseEntity.ok(adminDashboardService.getTransactionStatusAnalytics());
	}

	/**
	 * Transaction Type Analytics
	 */
	@GetMapping("/transaction-types")
	public ResponseEntity<Map<String, Long>> getTypeAnalytics() {
		return ResponseEntity.ok(adminDashboardService.getTransactionTypeAnalytics());
	}

	/**
	 * Top Active Users
	 */
	@GetMapping("/top-users")
	public ResponseEntity<List<TopActiveUserResponse>> getTopUsers() {
		return ResponseEntity.ok(adminDashboardService.getTopActiveUsers());
	}

	/**
	 * Today's Transaction Summary
	 */
	@GetMapping("/daily-summary")
	public ResponseEntity<DailyTransactionSummaryResponse> getDailySummary() {
		return ResponseEntity.ok(adminDashboardService.getDailySummary());
	}

	/**
	 * Date Range Dashboard Analytics
	 */
	@GetMapping("/range-summary")
	public ResponseEntity<DateRangeSummaryResponse> getRangeSummary(
			@RequestParam(name = "fromDate") LocalDate fromDate,
			@RequestParam(name = "toDate") LocalDate toDate) {
		return ResponseEntity.ok(adminDashboardService.getRangeSummary(fromDate, toDate));
	}

	/* Monthly Transaction Count Trends */
	@GetMapping("/monthly-trends")
	public ResponseEntity<List<MonthlyTransactionTrendResponse>> getMonthlyTransactionTrend() {
		return ResponseEntity.ok(adminDashboardService.getMonthlyTransactionTrend());
	}

	/* Monthly Transaction Volume Trends */
	@GetMapping("/monthly-volume")
	public ResponseEntity<List<MonthlyVolumeTrendResponse>> getMonthlyVolumeTrend() {
		return ResponseEntity.ok(adminDashboardService.getMonthlyVolumeTrend());
	}

	/* Failed Transaction Analytics */
	@GetMapping("/failure-analysis")
	public ResponseEntity<FailureAnalysisResponse> getFailureAnalysis() {
		return ResponseEntity.ok(adminDashboardService.getFailureAnalysis());
	}

	/* Volume By Transaction Type */
	@GetMapping("/volume-by-type")
	public ResponseEntity<List<TransactionVolumeByTypeResponse>> getTransactionVolumeByType() {
		return ResponseEntity.ok(adminDashboardService.getTransactionVolumeByType());
	}
}