package com.kusuma.payment_engine.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.entity.AccountSequence;
import com.kusuma.payment_engine.repository.AccountSequenceRepository;
import com.kusuma.payment_engine.service.AccountNumberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountNumberServiceImpl implements AccountNumberService {

	private final AccountSequenceRepository accountSequenceRepository;

	@Override
	@Transactional
	public String generateAccountNumber() {
		AccountSequence sequence = accountSequenceRepository.findByIdForUpdate(1L)
				.orElseThrow(() -> new IllegalStateException("Account sequence not initialized"));
		Long value = sequence.getNextValue();
		sequence.setNextValue(value + 1);
		accountSequenceRepository.save(sequence);
		return String.format("ACC%09d", value);
	}
}