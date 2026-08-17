package com.kusuma.payment_engine.exception;

public class InvalidCurrentPasswordException extends RuntimeException {

	public InvalidCurrentPasswordException(String message) {
		super(message);
	}
}