//package com.kusuma.payment_engine.service.impl;
//
//import java.math.BigDecimal;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.kusuma.payment_engine.dto.response.AccountResponse;
//import com.kusuma.payment_engine.entity.Account;
//import com.kusuma.payment_engine.entity.User;
//import com.kusuma.payment_engine.enums.AccountStatus;
//import com.kusuma.payment_engine.enums.AccountType;
//import com.kusuma.payment_engine.enums.CurrencyCode;
//import com.kusuma.payment_engine.enums.UserStatus;
//import com.kusuma.payment_engine.exception.AccountAlreadyClosedException;
//import com.kusuma.payment_engine.exception.AccountAlreadyExistsException;
//import com.kusuma.payment_engine.exception.AccountAlreadyFrozenException;
//import com.kusuma.payment_engine.exception.AccountBalanceNotZeroException;
//import com.kusuma.payment_engine.exception.AccountNotFoundException;
//import com.kusuma.payment_engine.exception.AccountNotFrozenException;
//import com.kusuma.payment_engine.exception.InvalidCredentialsException;
//import com.kusuma.payment_engine.exception.UserNotFoundException;
//import com.kusuma.payment_engine.repository.AccountRepository;
//import com.kusuma.payment_engine.repository.UserRepository;
//import com.kusuma.payment_engine.service.AccountNumberService;
//import com.kusuma.payment_engine.service.AccountService;
//import com.kusuma.payment_engine.util.AccountNumberGeneratorUtil;
//import com.kusuma.payment_engine.util.AccountValidationUtil;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class AccountServiceImpl implements AccountService {
//
//	private final AccountRepository accountRepository;
//	private final UserRepository userRepository;
//	private final AccountNumberService accountNumberService;
//
//	@Override
//	@Transactional
//	public AccountResponse createAccount() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		String email = authentication.getName();
//		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
//		// validateUser(user);
//		AccountValidationUtil.validateUserStatus(user);
//		if (accountRepository.existsByUser(user)) {
//			throw new AccountAlreadyExistsException("Account already exists");
//		}
//		Account account = Account.builder().accountNumber(accountNumberService.generateAccountNumber())
//				.balance(BigDecimal.ZERO).currency(CurrencyCode.INR).status(AccountStatus.ACTIVE)
//				.accountType(AccountType.PRIMARY).user(user).build();
//		Account savedAccount = accountRepository.save(account);
//		// savedAccount.setAccountNumber(AccountNumberGeneratorUtil.generate(savedAccount.getId()));
//		savedAccount = accountRepository.save(savedAccount);
//		return mapToResponse(savedAccount);
//	}
//
//	@Override
//	public AccountResponse getMyAccount() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		String email = authentication.getName();
//		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
//		// validateUser(user);
//		AccountValidationUtil.validateUserStatus(user);
//		Account account = accountRepository.findByUser(user)
//				.orElseThrow(() -> new AccountNotFoundException("No account found"));
//		return mapToResponse(account);
//	}
//
//	private void validateUser(User user) {
//		if (!Boolean.TRUE.equals(user.getEmailVerified())) {
//			throw new InvalidCredentialsException("Please verify your email first.");
//		}
//		if (user.getStatus() == UserStatus.LOCKED) {
//			throw new InvalidCredentialsException("Account is locked by administrator.");
//		}
//		if (user.getStatus() == UserStatus.INACTIVE) {
//			throw new InvalidCredentialsException("Account is inactive.");
//		}
//	}
//
//	private AccountResponse mapToResponse(Account account) {
//		return AccountResponse.builder().accountNumber(account.getAccountNumber()).balance(account.getBalance())
//				.currency(account.getCurrency()).status(account.getStatus()).build();
//	}
//
//	@Override
//	@Transactional
//	public void freezeAccount(Long accountId) {
//		Account account = accountRepository.findById(accountId)
//				.orElseThrow(() -> new AccountNotFoundException("Account not found"));
//		if (account.getStatus() == AccountStatus.CLOSED) {
//			throw new AccountAlreadyClosedException("Closed account cannot be frozen");
//		}
//		if (account.getStatus() == AccountStatus.FROZEN) {
//			throw new AccountAlreadyFrozenException("Account already frozen");
//		}
//		account.setStatus(AccountStatus.FROZEN);
//		accountRepository.save(account);
//	}
//
//	@Override
//	@Transactional
//	public void unfreezeAccount(Long accountId) {
//		Account account = accountRepository.findById(accountId)
//				.orElseThrow(() -> new AccountNotFoundException("Account not found"));
//		if (account.getStatus() == AccountStatus.CLOSED) {
//			throw new AccountAlreadyClosedException("Closed account cannot be reopened");
//		}
//		if (account.getStatus() != AccountStatus.FROZEN) {
//			throw new AccountNotFrozenException("Account is not frozen");
//		}
//		account.setStatus(AccountStatus.ACTIVE);
//		accountRepository.save(account);
//	}
//
//	@Override
//	@Transactional
//	public void closeAccount(Long accountId) {
//		Account account = accountRepository.findById(accountId)
//				.orElseThrow(() -> new AccountNotFoundException("Account not found"));
//		if (account.getStatus() == AccountStatus.CLOSED) {
//			throw new AccountAlreadyClosedException("Account already closed");
//		}
//		if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
//			throw new AccountBalanceNotZeroException("Account with balance cannot be closed");
//		}
//		account.setStatus(AccountStatus.CLOSED);
//		accountRepository.save(account);
//	}
//}

package com.kusuma.payment_engine.service.impl;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.dto.response.AccountResponse;
import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.AccountStatus;
import com.kusuma.payment_engine.enums.AccountType;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;
import com.kusuma.payment_engine.enums.CurrencyCode;
import com.kusuma.payment_engine.exception.AccountAlreadyClosedException;
import com.kusuma.payment_engine.exception.AccountAlreadyExistsException;
import com.kusuma.payment_engine.exception.AccountAlreadyFrozenException;
import com.kusuma.payment_engine.exception.AccountBalanceNotZeroException;
import com.kusuma.payment_engine.exception.AccountNotFoundException;
import com.kusuma.payment_engine.exception.AccountNotFrozenException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.repository.AccountRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AccountNumberService;
import com.kusuma.payment_engine.service.AccountService;
import com.kusuma.payment_engine.service.AuditLogService;
import com.kusuma.payment_engine.util.AccountValidationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final AccountNumberService accountNumberService;
	private final AuditLogService auditLogService;

	@Override
	@Transactional
	public AccountResponse createAccount() {
		User user = getAuthenticatedUser();
		if (accountRepository.existsByUser(user)) {
			throw new AccountAlreadyExistsException("Account already exists");
		}
		Account account = Account.builder().accountNumber(accountNumberService.generateAccountNumber())
				.balance(BigDecimal.ZERO).currency(CurrencyCode.INR).status(AccountStatus.ACTIVE)
				.accountType(AccountType.PRIMARY).user(user).build();
		Account savedAccount = accountRepository.save(account);
		auditLogService.log(user.getEmail(), AuditAction.CREATE_ACCOUNT, AuditEntityType.ACCOUNT, savedAccount.getId(),
				"Primary account created with account number " + savedAccount.getAccountNumber());
		return mapToResponse(savedAccount);
	}

	@Override
	public AccountResponse getMyAccount() {
		User user = getAuthenticatedUser();
		Account account = accountRepository.findByUser(user)
				.orElseThrow(() -> new AccountNotFoundException("No account found"));
		return mapToResponse(account);
	}

	@Override
	@Transactional
	public void freezeAccount(Long accountId) {
		Account account = getAccountByIdOrThrow(accountId);
		if (account.getStatus() == AccountStatus.CLOSED) {
			throw new AccountAlreadyClosedException("Closed account cannot be frozen");
		}
		if (account.getStatus() == AccountStatus.FROZEN) {
			throw new AccountAlreadyFrozenException("Account already frozen");
		}
		updateAccountStatus(account, AccountStatus.FROZEN);
		auditLogService.log(getCurrentAdminEmail(), AuditAction.FREEZE_ACCOUNT, AuditEntityType.ACCOUNT,
				account.getId(), "Account frozen. Account Number: " + account.getAccountNumber());
	}

	@Override
	@Transactional
	public void unfreezeAccount(Long accountId) {
		Account account = getAccountByIdOrThrow(accountId);
		if (account.getStatus() == AccountStatus.CLOSED) {
			throw new AccountAlreadyClosedException("Closed account cannot be reopened");
		}
		if (account.getStatus() != AccountStatus.FROZEN) {
			throw new AccountNotFrozenException("Account is not frozen");
		}
		updateAccountStatus(account, AccountStatus.ACTIVE);
		auditLogService.log(getCurrentAdminEmail(), AuditAction.UNFREEZE_ACCOUNT, AuditEntityType.ACCOUNT,
				account.getId(), "Account unfrozen. Account Number: " + account.getAccountNumber());
	}

	@Override
	@Transactional
	public void closeAccount(Long accountId) {
		Account account = getAccountByIdOrThrow(accountId);
		if (account.getStatus() == AccountStatus.CLOSED) {
			throw new AccountAlreadyClosedException("Account already closed");
		}
		if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
			throw new AccountBalanceNotZeroException("Account balance must be zero before account closure");
		}
		updateAccountStatus(account, AccountStatus.CLOSED);
		auditLogService.log(getCurrentAdminEmail(), AuditAction.CLOSE_ACCOUNT, AuditEntityType.ACCOUNT, account.getId(),
				"Account closed. Account Number: " + account.getAccountNumber());
	}

	private User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
		AccountValidationUtil.validateUserStatus(user);
		return user;
	}

	private Account getAccountByIdOrThrow(Long accountId) {
		return accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException("Account not found"));
	}

	private void updateAccountStatus(Account account, AccountStatus status) {
		account.setStatus(status);
		accountRepository.save(account);
	}

	private AccountResponse mapToResponse(Account account) {
		return AccountResponse.builder().accountNumber(account.getAccountNumber()).balance(account.getBalance())
				.currency(account.getCurrency()).status(account.getStatus()).build();
	}

	private String getCurrentAdminEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}