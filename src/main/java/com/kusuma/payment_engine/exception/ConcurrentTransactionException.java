package com.kusuma.payment_engine.exception;

public class ConcurrentTransactionException extends RuntimeException {

	public ConcurrentTransactionException(String message) {
		super(message);
	}
}