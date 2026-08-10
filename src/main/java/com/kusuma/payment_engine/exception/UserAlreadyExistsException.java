package com.kusuma.payment_engine.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {

        super(message);
    }
}