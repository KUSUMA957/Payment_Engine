package com.kusuma.payment_engine.exception;

public class InvalidTransactionException extends RuntimeException {

	public InvalidTransactionException(String message) {

		super(message);
	}
}