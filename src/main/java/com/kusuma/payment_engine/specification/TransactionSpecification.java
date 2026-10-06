package com.kusuma.payment_engine.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;

public class TransactionSpecification {

	public static Specification<Transaction> belongsToAccount(Account account) {
		return (root, query, cb) -> cb.or(cb.equal(root.get("senderAccount"), account),
				cb.equal(root.get("receiverAccount"), account));
	}

	public static Specification<Transaction> fromDate(LocalDateTime from) {
		return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("processedAt"), from);
	}

	public static Specification<Transaction> toDate(LocalDateTime to) {
		return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("processedAt"), to);
	}

	public static Specification<Transaction> type(TransactionType type) {
		return (root, query, cb) -> cb.equal(root.get("transactionType"), type);
	}

	public static Specification<Transaction> status(TransactionStatus status) {
		return (root, query, cb) -> cb.equal(root.get("status"), status);
	}
}