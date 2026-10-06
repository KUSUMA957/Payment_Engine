package com.kusuma.payment_engine.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.kusuma.payment_engine.dto.request.AdminStatementFilterRequest;
import com.kusuma.payment_engine.dto.request.BeneficiaryTransferRequest;
import com.kusuma.payment_engine.dto.request.StatementFilterRequest;
import com.kusuma.payment_engine.dto.request.TransactionAmountRequest;
import com.kusuma.payment_engine.dto.request.TransferRequest;
import com.kusuma.payment_engine.dto.response.TransactionResponse;

public interface TransactionService {

	TransactionResponse transfer(TransferRequest request);

	List<TransactionResponse> getMyTransactions();

	TransactionResponse getTransaction(String reference);

	TransactionResponse deposit(TransactionAmountRequest request);

	TransactionResponse withdraw(TransactionAmountRequest request);

	TransactionResponse transferToBeneficiary(BeneficiaryTransferRequest request);

	TransactionResponse getTransactionByReference(String transactionReference);

	List<TransactionResponse> getMiniStatement();

	Page<TransactionResponse> getStatement(StatementFilterRequest request);

	Page<TransactionResponse> getAdminStatement(AdminStatementFilterRequest request);

	Page<TransactionResponse> getTransactionsByUser(Long userId, int page, int size);

	TransactionResponse getAdminTransactionByReference(String reference);
}
