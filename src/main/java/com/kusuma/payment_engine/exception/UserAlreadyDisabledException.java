package com.kusuma.payment_engine.exception;

public class UserAlreadyDisabledException extends RuntimeException {

	public UserAlreadyDisabledException(String message) {
		super(message);
	}
}
