package com.kusuma.payment_engine.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Transaction;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.exception.InvalidTransactionException;
import com.kusuma.payment_engine.repository.AccountRepository;
import com.kusuma.payment_engine.repository.TransactionRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.CurrentUserService;
import com.kusuma.payment_engine.service.PdfStatementService;
import com.kusuma.payment_engine.util.AccountValidationUtil;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfStatementServiceImpl implements PdfStatementService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final CurrentUserService currentUserService;

	@Override
	public byte[] generateStatementPdf(LocalDate fromDate, LocalDate toDate) {
		if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
			throw new InvalidTransactionException("From date cannot be after To date");
		}
		User user = currentUserService.getAuthenticatedUser();
		AccountValidationUtil.validateUserStatus(user);
		Account account = accountRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		System.out.println("PDF Account ID = " + account.getId());
		System.out.println("PDF Account Number = " + account.getAccountNumber());
		List<Transaction> transactions = transactionRepository.findStatementTransactions(account,
				fromDate == null ? LocalDate.of(2000, 1, 1).atStartOfDay() : fromDate.atStartOfDay(),
				toDate == null ? LocalDate.now().plusYears(10).atTime(23, 59, 59) : toDate.atTime(23, 59, 59));
		System.out.println("Transactions Found: " + transactions.size());
		System.out.println("From Date = " + fromDate);
		System.out.println("To Date   = " + toDate);
		return buildPdf(user, account, transactions, fromDate, toDate);
	}

	private byte[] buildPdf(User user, Account account, List<Transaction> transactions, LocalDate fromDate,
			LocalDate toDate) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Document document = new Document();
			PdfWriter.getInstance(document, out);
			document.open();
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			document.add(new Paragraph("Account Statement", titleFont));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Customer: " + user.getFullName()));
			document.add(new Paragraph("Account Number: " + account.getAccountNumber()));
			document.add(new Paragraph("Generated At: " + LocalDateTime.now()));
			BigDecimal totalCredits = BigDecimal.ZERO;
			BigDecimal totalDebits = BigDecimal.ZERO;
			for (Transaction transaction : transactions) {
				if (account.equals(transaction.getReceiverAccount())) {
					totalCredits = totalCredits.add(transaction.getAmount());
				}
				if (account.equals(transaction.getSenderAccount())) {
					totalDebits = totalDebits.add(transaction.getAmount());
				}
			}
			document.add(new Paragraph("Transaction Count: " + transactions.size()));
			document.add(new Paragraph("Total Credits: ₹" + totalCredits));
			document.add(new Paragraph("Total Debits: ₹" + totalDebits));
			if (fromDate != null || toDate != null) {
				document.add(new Paragraph("Date Range: " + fromDate + " to " + toDate));
			}
			document.add(new Paragraph(" "));
			PdfPTable table = new PdfPTable(7);
			addHeader(table, "Reference");
			addHeader(table, "Type");
			addHeader(table, "Status");
			addHeader(table, "Amount");
			addHeader(table, "Currency");
			addHeader(table, "Description");
			addHeader(table, "Processed At");
			if (transactions.isEmpty()) {
				document.add(new Paragraph("No transactions found for the selected filters."));
				document.close();
				return out.toByteArray();
			}
			for (Transaction transaction : transactions) {
				table.addCell(transaction.getTransactionReference());
				table.addCell(transaction.getTransactionType().name());
				table.addCell(transaction.getStatus().name());
				table.addCell(transaction.getAmount().toString());
				table.addCell(transaction.getCurrency().name());
				table.addCell(transaction.getDescription() != null ? transaction.getDescription() : "-");
				table.addCell(String.valueOf(transaction.getProcessedAt()));
			}
			document.add(table);
			document.close();
			return out.toByteArray();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to generate statement PDF", ex);
		}
	}

	private void addHeader(PdfPTable table, String header) {
		PdfPCell cell = new PdfPCell(new Phrase(header));
		table.addCell(cell);
	}
}