package com.kusuma.payment_engine.exception;

public class AccountAlreadyExistsException extends RuntimeException {

	public AccountAlreadyExistsException(String message) {

		super(message);
	}
}