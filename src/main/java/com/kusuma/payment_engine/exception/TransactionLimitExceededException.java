package com.kusuma.payment_engine.exception;

public class TransactionLimitExceededException extends RuntimeException {

	public TransactionLimitExceededException(String message) {

		super(message);
	}
}