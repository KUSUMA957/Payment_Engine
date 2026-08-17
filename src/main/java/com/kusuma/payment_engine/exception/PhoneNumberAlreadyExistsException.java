package com.kusuma.payment_engine.exception;

public class PhoneNumberAlreadyExistsException extends RuntimeException {

	public PhoneNumberAlreadyExistsException(String message) {
		super(message);
	}
}