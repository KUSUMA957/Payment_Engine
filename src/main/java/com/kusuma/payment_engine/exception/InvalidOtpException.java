package com.kusuma.payment_engine.exception;

public class InvalidOtpException extends RuntimeException {

	public InvalidOtpException(String message) {

		super(message);
	}
}