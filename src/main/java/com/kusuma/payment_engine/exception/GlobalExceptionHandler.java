//package com.kusuma.payment_engine.exception;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authorization.AuthorizationDeniedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//	@ExceptionHandler(UserAlreadyExistsException.class)
//	public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
//		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//	}
//
//	@ExceptionHandler(InvalidOtpException.class)
//	public ResponseEntity<ErrorResponse> handleInvalidOtp(InvalidOtpException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
//				ex.getMessage());
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//	}
//
//	@ExceptionHandler(OtpExpiredException.class)
//	public ResponseEntity<ErrorResponse> handleOtpExpired(OtpExpiredException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
//				ex.getMessage());
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//	}
//
//	@ExceptionHandler(EmailDeliveryException.class)
//	public ResponseEntity<ErrorResponse> handleEmailDeliveryException(EmailDeliveryException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
//				ex.getMessage());
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//	}
//
//	@ExceptionHandler(InvalidCredentialsException.class)
//	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
//				ex.getMessage());
//		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//	}
//
//	@ExceptionHandler(EmailNotVerifiedException.class)
//	public ResponseEntity<ErrorResponse> handleEmailNotVerified(EmailNotVerifiedException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), ex.getMessage());
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//	}
//
//	@ExceptionHandler(AuthorizationDeniedException.class)
//	public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Access Denied");
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//	}
//
//	@ExceptionHandler(UserNotFoundException.class)
//	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//	}
//
//	@ExceptionHandler(NoChangesDetectedException.class)
//	public ResponseEntity<ErrorResponse> handleNoChangesDetected(NoChangesDetectedException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
//				ex.getMessage());
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//	}
//
//	@ExceptionHandler(PhoneNumberAlreadyExistsException.class)
//	public ResponseEntity<ErrorResponse> handlePhoneNumberAlreadyExists(PhoneNumberAlreadyExistsException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
//		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//	}
//
//	@ExceptionHandler(InvalidCurrentPasswordException.class)
//	public ResponseEntity<ErrorResponse> handleInvalidCurrentPassword(InvalidCurrentPasswordException ex) {
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(PasswordMismatchException.class)
//	public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException ex) {
//
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(SamePasswordException.class)
//	public ResponseEntity<ErrorResponse> handleSamePassword(SamePasswordException ex) {
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(SelfAdminActionException.class)
//	public ResponseEntity<ErrorResponse> handleSelfAdminAction(SelfAdminActionException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(UserAlreadyLockedException.class)
//	public ResponseEntity<ErrorResponse> handleUserAlreadyLocked(UserAlreadyLockedException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(UserNotLockedException.class)
//	public ResponseEntity<ErrorResponse> handleUserNotLocked(UserNotLockedException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(UserAlreadyDisabledException.class)
//	public ResponseEntity<ErrorResponse> handleUserAlreadyDisabled(UserAlreadyDisabledException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(UserAlreadyEnabledException.class)
//	public ResponseEntity<ErrorResponse> handleUserAlreadyEnabled(UserAlreadyEnabledException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(AccountAlreadyExistsException.class)
//	public ResponseEntity<ErrorResponse> handleAccountAlreadyExists(AccountAlreadyExistsException ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
//				ex.getMessage());
//		return ResponseEntity.badRequest().body(response);
//	}
//
//	@ExceptionHandler(AccountNotFoundException.class)
//	public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
//		return ResponseEntity.status(HttpStatus.NOT_FOUND)
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(AccountAlreadyFrozenException.class)
//	public ResponseEntity<ErrorResponse> handleAccountAlreadyFrozen(AccountAlreadyFrozenException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(AccountNotFrozenException.class)
//	public ResponseEntity<ErrorResponse> handleAccountNotFrozen(AccountNotFrozenException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(AccountAlreadyClosedException.class)
//	public ResponseEntity<ErrorResponse> handleAccountAlreadyClosed(AccountAlreadyClosedException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(AccountBalanceNotZeroException.class)
//	public ResponseEntity<ErrorResponse> handleAccountBalanceNotZero(AccountBalanceNotZeroException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(InsufficientBalanceException.class)
//	public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(InvalidTransactionException.class)
//	public ResponseEntity<ErrorResponse> handleInvalidTransaction(InvalidTransactionException ex) {
//		return ResponseEntity.badRequest()
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(TransactionNotFoundException.class)
//	public ResponseEntity<ErrorResponse> handleTransactionNotFound(TransactionNotFoundException ex) {
//		return ResponseEntity.status(HttpStatus.NOT_FOUND)
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(UnauthorizedTransactionAccessException.class)
//	public ResponseEntity<ErrorResponse> handleUnauthorizedTransaction(UnauthorizedTransactionAccessException ex) {
//		return ResponseEntity.status(HttpStatus.FORBIDDEN)
//				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), ex.getMessage()));
//	}
//
//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
//		Map<String, String> errors = new HashMap<>();
//		ex.getBindingResult().getFieldErrors()
//				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
//		return ResponseEntity.badRequest().body(errors);
//	}
//
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
//		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
//				ex.getMessage());
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//	}
//}

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

	@ExceptionHandler({ UserAlreadyExistsException.class, PhoneNumberAlreadyExistsException.class })
	public ResponseEntity<ErrorResponse> handleConflictExceptions(RuntimeException ex) {
		return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler({ InvalidOtpException.class, OtpExpiredException.class, NoChangesDetectedException.class,
			InvalidCurrentPasswordException.class, PasswordMismatchException.class, SamePasswordException.class,
			SelfAdminActionException.class, UserAlreadyLockedException.class, UserNotLockedException.class,
			UserAlreadyDisabledException.class, UserAlreadyEnabledException.class, AccountAlreadyExistsException.class,
			AccountAlreadyFrozenException.class, AccountNotFrozenException.class, AccountAlreadyClosedException.class,
			AccountBalanceNotZeroException.class, InsufficientBalanceException.class,
			InvalidTransactionException.class })
	public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler({ UserNotFoundException.class, AccountNotFoundException.class,
			TransactionNotFoundException.class })
	public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException ex) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler({ EmailNotVerifiedException.class, UnauthorizedTransactionAccessException.class,
			AuthorizationDeniedException.class })
	public ResponseEntity<ErrorResponse> handleForbiddenExceptions(Exception ex) {
		String message = ex instanceof AuthorizationDeniedException ? "Access Denied" : ex.getMessage();
		return buildErrorResponse(HttpStatus.FORBIDDEN, message);
	}

	@ExceptionHandler(EmailDeliveryException.class)
	public ResponseEntity<ErrorResponse> handleEmailDeliveryException(EmailDeliveryException ex) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	@ExceptionHandler(ConcurrentTransactionException.class)
	public ResponseEntity<ErrorResponse> handleConcurrentTransaction(ConcurrentTransactionException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage()));
	}

	@ExceptionHandler(NotificationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotificationNotFound(NotificationNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), status.value(), message);
		return ResponseEntity.status(status).body(response);
	}
}