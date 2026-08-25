package com.kusuma.payment_engine.exception;

public class AccountAlreadyClosedException extends RuntimeException {

	public AccountAlreadyClosedException(String message) {
		super(message);
	}
}