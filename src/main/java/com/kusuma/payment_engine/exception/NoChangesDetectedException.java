package com.kusuma.payment_engine.exception;

public class NoChangesDetectedException extends RuntimeException {

	public NoChangesDetectedException(String message) {
		super(message);
	}
}