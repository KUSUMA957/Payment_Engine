package com.kusuma.payment_engine.exception;

public class AccountNotFoundException extends RuntimeException {

	public AccountNotFoundException(String message) {

		super(message);
	}
}