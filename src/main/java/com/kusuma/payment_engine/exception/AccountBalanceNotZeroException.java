package com.kusuma.payment_engine.exception;

public class AccountBalanceNotZeroException extends RuntimeException {

	public AccountBalanceNotZeroException(String message) {
		super(message);
	}
}