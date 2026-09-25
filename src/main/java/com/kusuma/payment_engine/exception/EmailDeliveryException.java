package com.kusuma.payment_engine.exception;

public class EmailDeliveryException extends RuntimeException {
	public EmailDeliveryException(String message, Throwable cause) {
		// TODO Auto-generated constructor stub
		super(message, cause);
	}

	public EmailDeliveryException(String message) {
		super(message);
	}
}