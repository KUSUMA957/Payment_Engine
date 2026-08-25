package com.kusuma.payment_engine.service;

import com.kusuma.payment_engine.dto.response.AccountResponse;

public interface AccountService {

	AccountResponse createAccount();

	AccountResponse getMyAccount();
	
	void freezeAccount(Long accountId);

	void unfreezeAccount(Long accountId);

	void closeAccount(Long accountId);
}