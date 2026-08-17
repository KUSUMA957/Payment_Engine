package com.kusuma.payment_engine.exception;

public class UserNotLockedException extends RuntimeException {

	public UserNotLockedException(String message) {
		super(message);
	}
}
