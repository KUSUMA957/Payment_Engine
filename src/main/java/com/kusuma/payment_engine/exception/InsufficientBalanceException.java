package com.kusuma.payment_engine.exception;

public class InsufficientBalanceException extends RuntimeException {

	public InsufficientBalanceException(String message) {

		super(message);
	}
}