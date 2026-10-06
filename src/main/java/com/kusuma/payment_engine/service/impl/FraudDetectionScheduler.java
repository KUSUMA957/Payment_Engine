package com.kusuma.payment_engine.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.service.FraudDetectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudDetectionScheduler {

	private final FraudDetectionService fraudDetectionService;
	@Scheduled(cron = "0 0 * * * *")
    public void executeFraudAnalysis() {
        fraudDetectionService
                .analyzeRecentTransactions();
    }
}
