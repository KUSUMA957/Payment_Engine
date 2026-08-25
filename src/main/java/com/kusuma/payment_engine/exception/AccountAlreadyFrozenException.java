package com.kusuma.payment_engine.exception;

public class AccountAlreadyFrozenException extends RuntimeException {

	public AccountAlreadyFrozenException(String message) {
		super(message);
	}
}