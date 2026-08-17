package com.kusuma.payment_engine.exception;

public class SamePasswordException extends RuntimeException {

	public SamePasswordException(String message) {
		super(message);
	}
}
