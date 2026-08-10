package com.kusuma.payment_engine.exception;

public class EmailNotVerifiedException extends RuntimeException {

	public EmailNotVerifiedException(String message) {
		super(message);
	}
}