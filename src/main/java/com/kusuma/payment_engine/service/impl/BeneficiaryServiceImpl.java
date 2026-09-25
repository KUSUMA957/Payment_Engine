package com.kusuma.payment_engine.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.dto.request.AddBeneficiaryRequest;
import com.kusuma.payment_engine.dto.request.UpdateBeneficiaryRequest;
import com.kusuma.payment_engine.dto.response.BeneficiaryResponse;
import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Beneficiary;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.AccountStatus;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;
import com.kusuma.payment_engine.enums.NotificationType;
import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.exception.AccountNotFoundException;
import com.kusuma.payment_engine.exception.BeneficiaryNotFoundException;
import com.kusuma.payment_engine.exception.DuplicateBeneficiaryException;
import com.kusuma.payment_engine.exception.InvalidTransactionException;
import com.kusuma.payment_engine.exception.NoChangesDetectedException;
import com.kusuma.payment_engine.exception.UnauthorizedTransactionAccessException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.repository.AccountRepository;
import com.kusuma.payment_engine.repository.BeneficiaryRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AuditLogService;
import com.kusuma.payment_engine.service.BeneficiaryService;
import com.kusuma.payment_engine.service.NotificationService;
import com.kusuma.payment_engine.util.AccountValidationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

	private final BeneficiaryRepository beneficiaryRepository;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	private final AuditLogService auditLogService;
	private final NotificationService notificationService;

	@Override
	@Transactional
	public BeneficiaryResponse addBeneficiary(AddBeneficiaryRequest request) {
		User user = getAuthenticatedUser();
		Account myAcc = getUserAccount(user);
		validateBeneficiaryOperation(user, myAcc);
		String nickname = request.nickname().trim();
		Account beneficiaryAccount = accountRepository.findByAccountNumber(request.accountNumber())
				.orElseThrow(() -> new AccountNotFoundException("Account not found"));
		if (myAcc.getId().equals(beneficiaryAccount.getId())) {
			throw new InvalidTransactionException("You cannot add your own account as beneficiary");
		}
		if (beneficiaryAccount.getStatus() == AccountStatus.CLOSED) {
			throw new InvalidTransactionException("Closed account cannot be added as beneficiary");
		}
		if (beneficiaryRepository.existsByOwnerUserAndBeneficiaryAccount(user, beneficiaryAccount)) {
			throw new DuplicateBeneficiaryException("Beneficiary already exists");
		}
		Beneficiary beneficiary = Beneficiary.builder().ownerUser(user).beneficiaryAccount(beneficiaryAccount)
				.nickname(nickname).build();
		beneficiary = beneficiaryRepository.save(beneficiary);
		auditLogService.log(user.getEmail(), AuditAction.ADD_BENEFICIARY, AuditEntityType.ACCOUNT, beneficiary.getId(),
				"Beneficiary added: " + nickname);
		notificationService.createNotification(user, "Beneficiary Added",
				"Beneficiary " + nickname + " added successfully.", NotificationType.ACCOUNT, false);
		return mapToResponse(beneficiary);
	}

	@Override
	public List<BeneficiaryResponse> getMyBeneficiaries() {
		User user = getAuthenticatedUser();
		Account myAccount = getUserAccount(user);
		validateBeneficiaryOperation(user, myAccount);
		return beneficiaryRepository.findByOwnerUserOrderByCreatedAtDesc(user).stream().map(this::mapToResponse)
				.toList();
	}

	@Override
	public BeneficiaryResponse getBeneficiary(Long beneficiaryId) {
		User user = getAuthenticatedUser();
		Account myAccount = getUserAccount(user);
		validateBeneficiaryOperation(user, myAccount);
		Beneficiary beneficiary = beneficiaryRepository.findByIdAndOwnerUser(beneficiaryId, user)
				.orElseThrow(() -> new BeneficiaryNotFoundException("Beneficiary not found"));
		return mapToResponse(beneficiary);
	}

	@Override
	@Transactional
	public BeneficiaryResponse updateBeneficiary(Long beneficiaryId, UpdateBeneficiaryRequest request) {
		User user = getAuthenticatedUser();
		Account myAccount = getUserAccount(user);
		validateBeneficiaryOperation(user, myAccount);
		Beneficiary beneficiary = beneficiaryRepository.findByIdAndOwnerUser(beneficiaryId, user)
				.orElseThrow(() -> new BeneficiaryNotFoundException("Beneficiary not found"));
		String nickname = request.nickname().trim();
		if (beneficiary.getNickname().equalsIgnoreCase(nickname)) {
			throw new NoChangesDetectedException("No changes detected");
		}
		beneficiary.setNickname(nickname);
		beneficiary = beneficiaryRepository.save(beneficiary);
		auditLogService.log(user.getEmail(), AuditAction.UPDATE_BENEFICIARY, AuditEntityType.ACCOUNT,
				beneficiary.getId(), "Beneficiary updated: " + nickname);
		notificationService.createNotification(user, "Beneficiary Updated",
				"Beneficiary " + nickname + " updated successfully.", NotificationType.ACCOUNT, false);
		return mapToResponse(beneficiary);
	}

	@Override
	@Transactional
	public void deleteBeneficiary(Long beneficiaryId) {
		User user = getAuthenticatedUser();
		Account myAccount = getUserAccount(user);
		validateBeneficiaryOperation(user, myAccount);
		Beneficiary beneficiary = beneficiaryRepository.findByIdAndOwnerUser(beneficiaryId, user)
				.orElseThrow(() -> new BeneficiaryNotFoundException("Beneficiary not found"));
		String nickname = beneficiary.getNickname();
		auditLogService.log(user.getEmail(), AuditAction.DELETE_BENEFICIARY, AuditEntityType.ACCOUNT,
				beneficiary.getId(), "Beneficiary deleted: " + nickname);
		beneficiaryRepository.delete(beneficiary);
		notificationService.createNotification(user, "Beneficiary Removed",
				"Beneficiary " + nickname + " removed successfully.", NotificationType.ACCOUNT, false);
	}

	@Override
	public List<BeneficiaryResponse> getUserBeneficiaries(Long userId) {
		User admin = getAuthenticatedUser();
		if (admin.getRole() != Role.ADMIN) {
			throw new UnauthorizedTransactionAccessException("Only admins can access this resource");
		}
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		return beneficiaryRepository.findByOwnerUser(user).stream().map(this::mapToResponse).toList();
	}

	private User getAuthenticatedUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private BeneficiaryResponse mapToResponse(Beneficiary beneficiary) {
		return BeneficiaryResponse.builder().id(beneficiary.getId()).nickname(beneficiary.getNickname())
				.accountNumber(beneficiary.getBeneficiaryAccount().getAccountNumber())
				.beneficiaryName(beneficiary.getBeneficiaryAccount().getUser().getFullName()).build();
	}

	private void validateBeneficiaryOperation(User user, Account account) {
		AccountValidationUtil.validateUserStatus(user);
		if (account.getStatus() == AccountStatus.CLOSED) {
			throw new InvalidTransactionException("Cannot perform beneficiary operations on a closed account");
		}
	}

	private Account getUserAccount(User user) {
		return accountRepository.findByUser(user).orElseThrow(() -> new AccountNotFoundException("Account not found"));
	}
}