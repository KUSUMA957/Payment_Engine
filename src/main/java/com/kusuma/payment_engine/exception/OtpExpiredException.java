package com.kusuma.payment_engine.exception;

public class OtpExpiredException extends RuntimeException {

	public OtpExpiredException(String message) {

		super(message);
	}
}