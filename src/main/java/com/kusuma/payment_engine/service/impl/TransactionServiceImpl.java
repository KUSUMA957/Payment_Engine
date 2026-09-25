package com.kusuma.payment_engine.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.constants.SystemConstants;
import com.kusuma.payment_engine.dto.request.TransactionAmountRequest;
import com.kusuma.payment_engine.dto.request.TransferRequest;
import com.kusuma.payment_engine.dto.response.TransactionResponse;
import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;
import com.kusuma.payment_engine.enums.NotificationType;
import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;
import com.kusuma.payment_engine.exception.AccountNotFoundException;
import com.kusuma.payment_engine.exception.ConcurrentTransactionException;
import com.kusuma.payment_engine.exception.InsufficientBalanceException;
import com.kusuma.payment_engine.exception.InvalidTransactionException;
import com.kusuma.payment_engine.exception.TransactionNotFoundException;
import com.kusuma.payment_engine.exception.UnauthorizedTransactionAccessException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.repository.AccountRepository;
import com.kusuma.payment_engine.repository.TransactionRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AuditLogService;
import com.kusuma.payment_engine.service.NotificationService;
import com.kusuma.payment_engine.service.TransactionService;
import com.kusuma.payment_engine.util.AccountValidationUtil;
import com.kusuma.payment_engine.util.TransactionReferenceUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final AuditLogService auditLogService;
	private final NotificationService notificationService;

	@Override
	@Transactional
	public TransactionResponse transfer(TransferRequest request) {
		try {
			User user = getAuthenticatedUser();
			Account sender = getUserAccount(user);
			Account receiver = accountRepository.findByAccountNumber(request.receiverAccountNumber())
					.orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));
			AccountValidationUtil.validateUserAndAccountForTransactions(user, sender);
			AccountValidationUtil.validateAccountForTransactions(receiver);
			validateAmount(request.amount());
			if (sender.getAccountNumber().equals(receiver.getAccountNumber())) {
				throw new InvalidTransactionException("Self transfer is not allowed");
			}
			if (sender.getBalance().compareTo(request.amount()) < 0) {
				throw new InsufficientBalanceException("Insufficient balance");
			}
			sender.setBalance(sender.getBalance().subtract(request.amount()));
			receiver.setBalance(receiver.getBalance().add(request.amount()));
			accountRepository.save(sender);
			accountRepository.save(receiver);
			Transaction transaction = createTransaction(sender, receiver, request.amount(), sender.getCurrency(),
					TransactionType.TRANSFER, request.description());
			auditLogService.log(user.getEmail(), AuditAction.TRANSFER, AuditEntityType.TRANSACTION, transaction.getId(),
					"Transferred " + request.amount() + " to account " + receiver.getAccountNumber());
			notificationService.createNotification(user, "Transfer Successful",
					"₹" + request.amount() + " transferred to account " + receiver.getAccountNumber(),
					NotificationType.TRANSACTION, true);
			return mapToResponse(transaction);
		} catch (ObjectOptimisticLockingFailureException ex) {
			throw new ConcurrentTransactionException("Account was modified by another transaction. Please retry.");
		}
	}

	@Override
	@Transactional
	public TransactionResponse deposit(TransactionAmountRequest request) {
		try {
			User user = getAuthenticatedUser();
			Account account = getUserAccount(user);
			AccountValidationUtil.validateUserAndAccountForTransactions(user, account);
			validateAmount(request.amount());
			account.setBalance(account.getBalance().add(request.amount()));
			accountRepository.save(account);
			Transaction transaction = createTransaction(null, account, request.amount(), account.getCurrency(),
					TransactionType.DEPOSIT, request.description());
			auditLogService.log(user.getEmail(), AuditAction.DEPOSIT, AuditEntityType.TRANSACTION, transaction.getId(),
					"Deposited " + request.amount());
			notificationService.createNotification(user, "Deposit Successful",
					"₹" + request.amount() + " deposited successfully.", NotificationType.TRANSACTION, true);
			return mapToResponse(transaction);
		} catch (ObjectOptimisticLockingFailureException ex) {
			throw new ConcurrentTransactionException("Account was modified by another transaction. Please retry.");
		}
	}

	@Override
	@Transactional
	public TransactionResponse withdraw(TransactionAmountRequest request) {
		try {
			User user = getAuthenticatedUser();
			Account account = getUserAccount(user);
			AccountValidationUtil.validateUserAndAccountForTransactions(user, account);
			validateAmount(request.amount());
			if (account.getBalance().compareTo(request.amount()) < 0) {
				throw new InsufficientBalanceException("Insufficient balance");
			}
			account.setBalance(account.getBalance().subtract(request.amount()));
			accountRepository.save(account);
			Transaction transaction = createTransaction(account, null, request.amount(), account.getCurrency(),
					TransactionType.WITHDRAWAL, request.description());
			auditLogService.log(user.getEmail(), AuditAction.WITHDRAW, AuditEntityType.TRANSACTION, transaction.getId(),
					"Withdrawn " + request.amount());
			notificationService.createNotification(user, "Withdrawal Successful",
					"₹" + request.amount() + " withdrawn successfully.", NotificationType.TRANSACTION, true);
			return mapToResponse(transaction);
		} catch (ObjectOptimisticLockingFailureException ex) {
			throw new ConcurrentTransactionException("Account was modified by another transaction. Please retry.");
		}
	}

	@Override
	public List<TransactionResponse> getMyTransactions() {
		User user = getAuthenticatedUser();
		Account account = getUserAccount(user);
		return transactionRepository.findBySenderAccountOrReceiverAccount(account, account).stream()
				.map(this::mapToResponse).toList();
	}

	@Override
	public TransactionResponse getTransaction(String reference) {
		Transaction transaction = transactionRepository.findByTransactionReference(reference)
				.orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
		User user = getAuthenticatedUser();
		boolean isAdmin = user.getRole() == Role.ADMIN;
		if (!isAdmin && !isTransactionOwner(transaction, user)) {
			throw new UnauthorizedTransactionAccessException("You are not authorized to view this transaction.");
		}
		return mapToResponse(transaction);
	}

	private User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private Account getUserAccount(User user) {
		return accountRepository.findByUser(user).orElseThrow(() -> new AccountNotFoundException("Account not found"));
	}

	private void validateAmount(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Amount must be greater than zero");
		}
	}

	private Transaction createTransaction(Account sender, Account receiver, BigDecimal amount,
			com.kusuma.payment_engine.enums.CurrencyCode currency, TransactionType type, String description) {
		Transaction transaction = Transaction.builder().transactionReference(TransactionReferenceUtil.generate())
				.senderAccount(sender).receiverAccount(receiver).amount(amount).currency(currency).transactionType(type)
				.status(TransactionStatus.SUCCESS).description(description).processedAt(LocalDateTime.now()).build();
		return transactionRepository.save(transaction);
	}

	private boolean isTransactionOwner(Transaction transaction, User user) {
		if (transaction.getSenderAccount() != null
				&& transaction.getSenderAccount().getUser().getId().equals(user.getId())) {
			return true;
		}
		return transaction.getReceiverAccount() != null
				&& transaction.getReceiverAccount().getUser().getId().equals(user.getId());
	}

	private TransactionResponse mapToResponse(Transaction transaction) {
		return TransactionResponse.builder().transactionReference(transaction.getTransactionReference())
				.senderAccountNumber(
						transaction.getSenderAccount() != null ? transaction.getSenderAccount().getAccountNumber()
								: SystemConstants.SYSTEM_ACCOUNT)
				.receiverAccountNumber(
						transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getAccountNumber()
								: SystemConstants.SYSTEM_ACCOUNT)
				.amount(transaction.getAmount()).currency(transaction.getCurrency())
				.transactionType(transaction.getTransactionType()).status(transaction.getStatus())
				.description(transaction.getDescription()).processedAt(transaction.getProcessedAt()).build();
	}
}