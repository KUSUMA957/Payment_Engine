package com.kusuma.payment_engine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kusuma.payment_engine.entity.FraudAlert;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.FraudType;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

	boolean existsByTransactionAndFraudType(Transaction transaction, FraudType fraudType);

	boolean existsByUserAndFraudTypeAndResolvedFalse(User user, FraudType fraudType);

	List<FraudAlert> findByResolvedFalseOrderByCreatedAtDesc();

	@Query("""
			SELECT COALESCE(
			       SUM(f.riskScore),
			       0)
			FROM FraudAlert f
			WHERE
			f.user = :user
			AND
			f.resolved = false
			""")
	Integer getUserRiskScore(@Param("user") User user);

	List<FraudAlert> findByResolvedFalseOrderByRiskScoreDesc();

	@Query("""
			SELECT DISTINCT f.user
			FROM FraudAlert f
			WHERE f.resolved = false
			""")
	List<User> findUsersWithActiveAlerts();
}