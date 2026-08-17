package com.kusuma.payment_engine.exception;

public class UserAlreadyLockedException extends RuntimeException {

	public UserAlreadyLockedException(String message) {
		super(message);
	}
}
