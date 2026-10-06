package com.kusuma.payment_engine.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.entity.TransactionLimit;
import com.kusuma.payment_engine.enums.TransactionLimitType;
import com.kusuma.payment_engine.enums.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

	Optional<Transaction> findByTransactionReference(String reference);

	List<Transaction> findBySenderAccountOrReceiverAccount(Account sender, Account receiver);

	List<Transaction> findBySenderAccountOrReceiverAccountOrderByProcessedAtDesc(Account senderAccount,
			Account receiverAccount);

	List<Transaction> findTop10BySenderAccountOrReceiverAccountOrderByProcessedAtDesc(Account senderAccount,
			Account receiverAccount);

	@Query("""
			SELECT t FROM Transaction t WHERE
			(
			    t.senderAccount = :account
			    OR
			    t.receiverAccount = :account
			)
			AND
			t.processedAt BETWEEN :fromDate AND :toDate
			ORDER BY t.processedAt DESC
			""")
	List<Transaction> findStatementTransactions(@Param("account") Account account,
			@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

	long countByStatus(TransactionStatus status);

	@Query("""
			SELECT COALESCE(SUM(t.amount),0)
			FROM Transaction t
			WHERE t.status = 'SUCCESS'
			""")
	BigDecimal getTotalTransactionVolume();

	@Query("""
			SELECT COUNT(t)
			FROM Transaction t
			WHERE DATE(t.processedAt) = CURRENT_DATE
			""")
	long getTodayTransactionCount();

	@Query("""
			SELECT COALESCE(SUM(t.amount),0)
			FROM Transaction t
			WHERE DATE(t.processedAt) = CURRENT_DATE
			""")
	BigDecimal getTodayTransactionVolume();

	@Query("""
			SELECT t.status,
			       COUNT(t)
			FROM Transaction t
			GROUP BY t.status
			""")
	List<Object[]> getStatusAnalytics();

	@Query("""
			SELECT t.transactionType,
			       COUNT(t)
			FROM Transaction t
			GROUP BY t.transactionType
			""")
	List<Object[]> getTypeAnalytics();

	@Query("""
			SELECT
			u.id,
			u.fullName,
			COUNT(t)
			FROM Transaction t
			LEFT JOIN t.senderAccount sa
			LEFT JOIN sa.user u
			GROUP BY u.id,u.fullName
			ORDER BY COUNT(t) DESC
			LIMIT 5
			""")
	List<Object[]> getTopActiveUsers();

	@Query("""
			SELECT COUNT(t)
			FROM Transaction t
			WHERE t.processedAt BETWEEN :fromDate AND :toDate
			""")
	long countTransactionsInRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

	@Query("""
			SELECT COUNT(t)
			FROM Transaction t
			WHERE t.status = :status
			AND t.processedAt BETWEEN :fromDate AND :toDate
			""")
	long countTransactionsByStatusInRange(@Param("status") TransactionStatus status,
			@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

	@Query("""
			SELECT COALESCE(SUM(t.amount),0)
			FROM Transaction t
			WHERE t.status = 'SUCCESS'
			AND t.processedAt BETWEEN :fromDate AND :toDate
			""")
	BigDecimal getVolumeInRange(@Param("fromDate") LocalDateTime fromDate,

			@Param("toDate") LocalDateTime toDate);

	@Query("""
			SELECT
			DATE_FORMAT(t.processedAt,'%Y-%m'),
			COUNT(t)
			FROM Transaction t
			GROUP BY DATE_FORMAT(t.processedAt,'%Y-%m')
			ORDER BY DATE_FORMAT(t.processedAt,'%Y-%m')
			""")
	List<Object[]> getMonthlyTransactionTrend();

	@Query("""
			SELECT
			DATE_FORMAT(t.processedAt,'%Y-%m'),
			COALESCE(SUM(t.amount),0)
			FROM Transaction t
			WHERE t.status='SUCCESS'
			GROUP BY DATE_FORMAT(t.processedAt,'%Y-%m')
			ORDER BY DATE_FORMAT(t.processedAt,'%Y-%m')
			""")
	List<Object[]> getMonthlyVolumeTrend();

	@Query("""
			SELECT
			t.transactionType,
			COALESCE(SUM(t.amount),0)
			FROM Transaction t
			WHERE t.status='SUCCESS'
			GROUP BY t.transactionType
			""")
	List<Object[]> getTransactionVolumeByType();

	@Query("""
			SELECT COALESCE(SUM(t.amount),0)
			FROM Transaction t
			WHERE
			t.senderAccount = :account
			AND
			t.transactionType = 'TRANSFER'
			AND
			t.status = 'SUCCESS'
			AND
			DATE(t.processedAt)=CURRENT_DATE
			""")
	BigDecimal getTodayTransferAmount(@Param("account") Account account);

	@Query("""
			SELECT COALESCE(SUM(t.amount),0)
			FROM Transaction t
			WHERE
			t.senderAccount = :account
			AND
			t.transactionType='TRANSFER'
			AND
			t.status='SUCCESS'
			AND
			YEAR(t.processedAt)=YEAR(CURRENT_DATE)
			AND
			MONTH(t.processedAt)=MONTH(CURRENT_DATE)
			""")
	BigDecimal getMonthlyTransferAmount(@Param("account") Account account);

}
