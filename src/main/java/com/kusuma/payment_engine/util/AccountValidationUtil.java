package com.kusuma.payment_engine.util;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.AccountStatus;
import com.kusuma.payment_engine.enums.UserStatus;
import com.kusuma.payment_engine.exception.InvalidCredentialsException;

public final class AccountValidationUtil {

	private AccountValidationUtil() {
	}

	public static void validateUserStatus(User user) {
		if (!Boolean.TRUE.equals(user.getEmailVerified())) {
			throw new InvalidCredentialsException("Please verify your email first.");
		}
		if (user.getStatus() == UserStatus.LOCKED) {
			throw new InvalidCredentialsException("Account is locked by administrator.");
		}
		if (user.getStatus() == UserStatus.INACTIVE) {
			throw new InvalidCredentialsException("Account is inactive.");
		}
	}

	public static void validateAccountForTransactions(Account account) {
		if (account.getStatus() == AccountStatus.FROZEN) {
			throw new InvalidCredentialsException("Account is frozen.");
		}
		if (account.getStatus() == AccountStatus.CLOSED) {
			throw new InvalidCredentialsException("Account is closed.");
		}
	}
}