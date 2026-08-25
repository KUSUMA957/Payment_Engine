package com.kusuma.payment_engine.exception;

public class AccountNotFrozenException extends RuntimeException {

	public AccountNotFrozenException(String message) {
		super(message);
	}
}