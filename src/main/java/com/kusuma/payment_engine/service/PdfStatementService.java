package com.kusuma.payment_engine.service;

import java.time.LocalDate;

public interface PdfStatementService {
	byte[] generateStatementPdf(LocalDate fromDate, LocalDate toDate);
}