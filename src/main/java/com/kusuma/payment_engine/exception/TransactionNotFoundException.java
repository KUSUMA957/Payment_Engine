package com.kusuma.payment_engine.exception;

public class TransactionNotFoundException extends RuntimeException {

	public TransactionNotFoundException(String message) {

		super(message);
	}
}