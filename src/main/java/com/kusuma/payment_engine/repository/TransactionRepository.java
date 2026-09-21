package com.kusuma.payment_engine.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	Optional<Transaction> findByTransactionReference(String reference);

	List<Transaction> findBySenderAccountOrReceiverAccount(Account sender, Account receiver);
}
