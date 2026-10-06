package com.kusuma.payment_engine.service;

import com.kusuma.payment_engine.entity.Transaction;

public interface FraudDetectionService {
	
	void evaluateTransaction(Transaction transaction);

	void analyzeRecentTransactions();
}