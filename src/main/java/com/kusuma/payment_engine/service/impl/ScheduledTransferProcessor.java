package com.kusuma.payment_engine.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Beneficiary;
import com.kusuma.payment_engine.entity.ScheduledTransfer;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;
import com.kusuma.payment_engine.enums.NotificationType;
import com.kusuma.payment_engine.enums.ScheduledTransferStatus;
import com.kusuma.payment_engine.enums.TransactionStatus;
import com.kusuma.payment_engine.enums.TransactionType;
import com.kusuma.payment_engine.exception.InsufficientBalanceException;
import com.kusuma.payment_engine.repository.AccountRepository;
import com.kusuma.payment_engine.repository.ScheduledTransferRepository;
import com.kusuma.payment_engine.repository.TransactionRepository;
import com.kusuma.payment_engine.service.AuditLogService;
import com.kusuma.payment_engine.service.EmailService;
import com.kusuma.payment_engine.service.NotificationService;
import com.kusuma.payment_engine.util.AccountValidationUtil;
import com.kusuma.payment_engine.util.EmailTemplateUtil;
import com.kusuma.payment_engine.util.TransactionReferenceUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduledTransferProcessor {

	private final ScheduledTransferRepository scheduledTransferRepository;
	private final TransactionRepository transactionRepository;
	private final AccountRepository accountRepository;

	private final AuditLogService auditLogService;
	private final NotificationService notificationService;
	private final EmailService emailService;

	@Scheduled(fixedRate = 60000)
	@Transactional
	public void processScheduledTransfers() {
		List<ScheduledTransfer> transfers = scheduledTransferRepository
				.findByStatusAndScheduledAtLessThanEqual(ScheduledTransferStatus.PENDING, LocalDate.now());
		for (ScheduledTransfer transfer : transfers) {
			try {
				executeTransfer(transfer);

			} catch (Exception ex) {
				transfer.setStatus(ScheduledTransferStatus.FAILED);
				transfer.setFailureReason(ex.getMessage());
				scheduledTransferRepository.save(transfer);
				notificationService.createNotification(transfer.getUser(), "Scheduled Transfer Failed", ex.getMessage(),
						NotificationType.TRANSACTION, false);
			}
		}
	}

	private void executeTransfer(ScheduledTransfer scheduledTransfer) {
		User senderUser = scheduledTransfer.getUser();
		Beneficiary beneficiary = scheduledTransfer.getBeneficiary();
		Account sender = getSenderAccount(senderUser);
		Account receiver = beneficiary.getBeneficiaryAccount();
		AccountValidationUtil.validateUserAndAccountForTransactions(senderUser, sender);
		AccountValidationUtil.validateAccountForTransactions(receiver);
		if (sender.getBalance().compareTo(scheduledTransfer.getAmount()) < 0) {
			throw new InsufficientBalanceException("Insufficient balance for scheduled transfer");
		}
		sender.setBalance(sender.getBalance().subtract(scheduledTransfer.getAmount()));
		receiver.setBalance(receiver.getBalance().add(scheduledTransfer.getAmount()));
		accountRepository.save(sender);
		accountRepository.save(receiver);
		Transaction transaction = createTransaction(sender, receiver, scheduledTransfer);
		User receiverUser = receiver.getUser();
		notificationService.createNotification(senderUser, "Amount Debited",
				"Scheduled transfer of ₹" + scheduledTransfer.getAmount() + " executed successfully.",
				NotificationType.TRANSACTION, false);
		notificationService.createNotification(receiverUser, "Amount Credited",
				"₹" + scheduledTransfer.getAmount() + " credited to your account.", NotificationType.TRANSACTION,
				false);
		String debitEmailBody = EmailTemplateUtil.debitEmail(senderUser.getFullName(),
				transaction.getTransactionReference(), transaction.getAmount(), sender.getAccountNumber(),
				receiver.getAccountNumber());
		emailService.sendEmail(senderUser.getEmail(), "Payment Engine - Amount Debited", debitEmailBody);
		String creditEmailBody = EmailTemplateUtil.creditEmail(receiverUser.getFullName(),
				transaction.getTransactionReference(), transaction.getAmount(), sender.getAccountNumber(),
				receiver.getAccountNumber());
		emailService.sendEmail(receiverUser.getEmail(), "Payment Engine - Amount Credited", creditEmailBody);
		auditLogService.log(senderUser.getEmail(), AuditAction.EXECUTE_SCHEDULED_TRANSFER, AuditEntityType.TRANSACTION,
				transaction.getId(), "Scheduled transfer executed");
		scheduledTransfer.setStatus(ScheduledTransferStatus.COMPLETED);
		scheduledTransfer.setExecutedAt(LocalDateTime.now());
		scheduledTransferRepository.save(scheduledTransfer);
	}

	private Account getSenderAccount(User user) {
		return accountRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Sender account not found"));
	}

	private Transaction createTransaction(Account sender, Account receiver, ScheduledTransfer transfer) {
		Transaction transaction = Transaction.builder().transactionReference(TransactionReferenceUtil.generate())
				.senderAccount(sender).receiverAccount(receiver).amount(transfer.getAmount())
				.currency(sender.getCurrency()).transactionType(TransactionType.TRANSFER)
				.status(TransactionStatus.SUCCESS).description(transfer.getDescription())
				.processedAt(LocalDateTime.now()).build();
		return transactionRepository.save(transaction);
	}
}