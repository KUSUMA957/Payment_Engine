package com.kusuma.payment_engine.exception;

public class UnauthorizedTransactionAccessException extends RuntimeException {

	public UnauthorizedTransactionAccessException(String message) {
		super(message);
	}
}
