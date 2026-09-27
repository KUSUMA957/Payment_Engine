package com.kusuma.payment_engine.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.dto.request.CreateScheduledTransferRequest;
import com.kusuma.payment_engine.dto.response.ScheduledTransferResponse;
import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Beneficiary;
import com.kusuma.payment_engine.entity.ScheduledTransfer;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;
import com.kusuma.payment_engine.enums.NotificationType;
import com.kusuma.payment_engine.enums.ScheduledTransferStatus;
import com.kusuma.payment_engine.enums.TransferFrequency;
import com.kusuma.payment_engine.exception.AccountNotFoundException;
import com.kusuma.payment_engine.exception.BeneficiaryNotFoundException;
import com.kusuma.payment_engine.exception.InvalidTransactionException;
import com.kusuma.payment_engine.exception.ScheduledTransferNotFoundException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.repository.AccountRepository;
import com.kusuma.payment_engine.repository.BeneficiaryRepository;
import com.kusuma.payment_engine.repository.ScheduledTransferRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AuditLogService;
import com.kusuma.payment_engine.service.NotificationService;
import com.kusuma.payment_engine.service.ScheduledTransferService;
import com.kusuma.payment_engine.util.AccountValidationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduledTransferServiceImpl implements ScheduledTransferService {

	private final ScheduledTransferRepository scheduledTransferRepository;
	private final BeneficiaryRepository beneficiaryRepository;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final AuditLogService auditLogService;
	private final NotificationService notificationService;

	@Override
	public ScheduledTransferResponse scheduleTransfer(CreateScheduledTransferRequest request) {
		User user = getAuthenticatedUser();
		Account account = getUserAccount(user);
		AccountValidationUtil.validateUserAndAccountForTransactions(user, account);
		if (request.scheduledAt().isBefore(LocalDate.now())) {
			throw new InvalidTransactionException("Scheduled date must be in the future");
		}
		Beneficiary beneficiary = beneficiaryRepository.findByIdAndOwnerUser(request.beneficiaryId(), user)
				.orElseThrow(() -> new BeneficiaryNotFoundException("Beneficiary not found"));
		AccountValidationUtil.validateAccountForTransactions(beneficiary.getBeneficiaryAccount());
		if (request.frequency() == TransferFrequency.ONE_TIME && request.endDate() != null) {
			throw new InvalidTransactionException("End date is not allowed for ONE_TIME transfers");
		}
		if (request.frequency() != TransferFrequency.ONE_TIME && request.endDate() == null) {
			throw new InvalidTransactionException("End date is required for recurring transfers");
		}
		if (request.endDate() != null && request.endDate().isBefore(request.scheduledAt())) {
			throw new InvalidTransactionException("End date cannot be before scheduled date");
		}
		ScheduledTransfer transfer = ScheduledTransfer.builder().user(user).beneficiary(beneficiary)
				.amount(request.amount()).description(request.description()).scheduledAt(request.scheduledAt())
				.nextExecutionDate(request.scheduledAt()).frequency(request.frequency()).endDate(request.endDate())
				.status(ScheduledTransferStatus.PENDING).build();
		transfer = scheduledTransferRepository.save(transfer);
		auditLogService.log(user.getEmail(), AuditAction.SCHEDULE_TRANSFER, AuditEntityType.TRANSACTION,
				transfer.getId(), "Scheduled transfer to " + beneficiary.getNickname());
		String message = "Scheduled transfer created. " + "Amount: ₹" + request.amount() + ", Beneficiary: "
				+ beneficiary.getNickname() + ", Frequency: " + request.frequency() + ", Start Date: "
				+ request.scheduledAt() + (request.endDate() != null ? ", End Date: " + request.endDate() : "");
		notificationService.createNotification(user, "Scheduled Transfer Created", message,
				NotificationType.TRANSACTION, false);
		return mapToResponse(transfer);
	}

	@Override
	public List<ScheduledTransferResponse> getMyScheduledTransfers() {
		User user = getAuthenticatedUser();
		return scheduledTransferRepository.findByUserOrderByScheduledAtDesc(user).stream().map(this::mapToResponse)
				.toList();
	}

	@Override
	public ScheduledTransferResponse getScheduledTransfer(Long transferId) {
		User user = getAuthenticatedUser();
		ScheduledTransfer transfer = scheduledTransferRepository.findByIdAndUser(transferId, user)
				.orElseThrow(() -> new ScheduledTransferNotFoundException("Scheduled transfer not found"));
		return mapToResponse(transfer);
	}

	@Override
	public void cancelScheduledTransfer(Long transferId) {
		User user = getAuthenticatedUser();
		ScheduledTransfer transfer = scheduledTransferRepository.findByIdAndUser(transferId, user)
				.orElseThrow(() -> new ScheduledTransferNotFoundException("Scheduled transfer not found"));
		if (transfer.getStatus() != ScheduledTransferStatus.PENDING) {
			throw new InvalidTransactionException("Only pending transfers can be cancelled");
		}
		transfer.setStatus(ScheduledTransferStatus.CANCELLED);
		scheduledTransferRepository.save(transfer);
		auditLogService.log(user.getEmail(), AuditAction.CANCEL_SCHEDULED_TRANSFER, AuditEntityType.TRANSACTION,
				transfer.getId(), "Cancelled scheduled transfer");
	}

	private User getAuthenticatedUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private Account getUserAccount(User user) {
		return accountRepository.findByUser(user).orElseThrow(() -> new AccountNotFoundException("Account not found"));
	}

	private ScheduledTransferResponse mapToResponse(ScheduledTransfer transfer) {
		return ScheduledTransferResponse.builder().id(transfer.getId()).beneficiaryId(transfer.getBeneficiary().getId())
				.beneficiaryNickname(transfer.getBeneficiary().getNickname())
				.beneficiaryAccountNumber(transfer.getBeneficiary().getBeneficiaryAccount().getAccountNumber())
				.amount(transfer.getAmount()).description(transfer.getDescription())
				.scheduledAt(transfer.getScheduledAt()).nextExecutionDate(transfer.getNextExecutionDate())
				.endDate(transfer.getEndDate()).executedAt(transfer.getExecutedAt())
				.failureReason(transfer.getFailureReason()).frequency(transfer.getFrequency())
				.status(transfer.getStatus()).build();
	}
}