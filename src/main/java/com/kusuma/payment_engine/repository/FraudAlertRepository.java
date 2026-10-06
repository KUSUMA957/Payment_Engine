package com.kusuma.payment_engine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusuma.payment_engine.entity.FraudAlert;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.FraudType;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

	boolean existsByTransactionAndFraudType(Transaction transaction, FraudType fraudType);

	boolean existsByUserAndFraudTypeAndResolvedFalse(User user, FraudType fraudType);

	List<FraudAlert> findByResolvedFalseOrderByCreatedAtDesc();
}