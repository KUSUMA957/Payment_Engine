package com.kusuma.payment_engine.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Transaction;

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
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);
}
