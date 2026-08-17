package com.kusuma.payment_engine.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(InvalidOtpException.class)
	public ResponseEntity<ErrorResponse> handleInvalidOtp(InvalidOtpException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<ErrorResponse> handleOtpExpired(OtpExpiredException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(EmailDeliveryException.class)
	public ResponseEntity<ErrorResponse> handleEmailDeliveryException(EmailDeliveryException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	@ExceptionHandler(EmailNotVerifiedException.class)
	public ResponseEntity<ErrorResponse> handleEmailNotVerified(EmailNotVerifiedException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Access Denied");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(NoChangesDetectedException.class)
	public ResponseEntity<ErrorResponse> handleNoChangesDetected(NoChangesDetectedException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(PhoneNumberAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handlePhoneNumberAlreadyExists(PhoneNumberAlreadyExistsException ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(InvalidCurrentPasswordException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCurrentPassword(InvalidCurrentPasswordException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(PasswordMismatchException.class)
	public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(SamePasswordException.class)
	public ResponseEntity<ErrorResponse> handleSamePassword(SamePasswordException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}