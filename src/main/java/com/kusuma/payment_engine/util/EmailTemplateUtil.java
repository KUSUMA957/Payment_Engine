package com.kusuma.payment_engine.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class EmailTemplateUtil {

	private EmailTemplateUtil() {
	}

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

	public static String transferEmail(String customerName, String reference, BigDecimal amount, String senderAccount,
			String receiverAccount) {
		return """
				Hello %s,

				Your transfer was completed successfully.

				Transaction Details
				-------------------------------------

				Reference Number : %s
				Transaction Type : TRANSFER
				Amount           : ₹%s

				From Account     : %s
				To Account       : %s

				Status           : SUCCESS
				Date & Time      : %s

				If you did not initiate this transaction,
				please contact support immediately.

				Thank you,
				Payment Engine Team
				""".formatted(customerName, reference, amount, maskAccount(senderAccount), maskAccount(receiverAccount),
				LocalDateTime.now().format(FORMATTER));
	}

	public static String creditEmail(String customerName, String reference, BigDecimal amount, String senderAccount,
			String receiverAccount) {
		return """
				Hello %s,

				An amount has been credited to your account.

				Transaction Details
				-------------------------------------

				Reference Number : %s
				Transaction Type : CREDIT
				Amount           : ₹%s

				From Account     : %s
				To Account       : %s

				Status           : SUCCESS
				Date & Time      : %s

				Thank you,
				Payment Engine Team
				""".formatted(customerName, reference, amount, maskAccount(senderAccount), maskAccount(receiverAccount),
				LocalDateTime.now().format(FORMATTER));
	}

	public static String debitEmail(String customerName, String reference, BigDecimal amount, String senderAccount,
			String receiverAccount) {

		return """
				Hello %s,

				An amount has been debited from your account.

				Transaction Details
				-------------------------------------

				Reference Number : %s
				Transaction Type : DEBIT
				Amount           : ₹%s

				From Account     : %s
				To Account       : %s

				Status           : SUCCESS
				Date & Time      : %s

				If you did not perform this transaction,
				contact support immediately.

				Thank you,
				Payment Engine Team
				""".formatted(customerName, reference, amount, maskAccount(senderAccount), maskAccount(receiverAccount),
				LocalDateTime.now().format(FORMATTER));
	}

	public static String depositEmail(String customerName, String reference, BigDecimal amount, String accountNumber) {
		return """
				Hello %s,

				Your deposit was completed successfully.

				Transaction Details
				-------------------------------------

				Reference Number : %s
				Transaction Type : DEPOSIT
				Amount           : ₹%s

				Account Number   : %s

				Status           : SUCCESS
				Date & Time      : %s

				Thank you,
				Payment Engine Team
				""".formatted(customerName, reference, amount, maskAccount(accountNumber),
				LocalDateTime.now().format(FORMATTER));
	}

	public static String withdrawalEmail(String customerName, String reference, BigDecimal amount,
			String accountNumber) {
		return """
				Hello %s,

				Your withdrawal was completed successfully.

				Transaction Details
				-------------------------------------

				Reference Number : %s
				Transaction Type : WITHDRAWAL
				Amount           : ₹%s

				Account Number   : %s

				Status           : SUCCESS
				Date & Time      : %s

				If you did not perform this transaction,
				please contact support immediately.

				Thank you,
				Payment Engine Team
				""".formatted(customerName, reference, amount, maskAccount(accountNumber),
				LocalDateTime.now().format(FORMATTER));
	}

	public static String passwordChangedEmail(String customerName) {

		return """
				Hello %s,

				Your account password was changed successfully.

				Date & Time : %s

				If you did not initiate this change,
				reset your password immediately and contact support.

				Thank you,
				Payment Engine Team
				""".formatted(customerName, LocalDateTime.now().format(FORMATTER));
	}

	private static String maskAccount(String accountNumber) {
		return "XXXXXX" + accountNumber.substring(accountNumber.length() - 4);
	}
}