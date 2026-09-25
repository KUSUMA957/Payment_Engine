package com.kusuma.payment_engine.service;

import java.util.List;

import com.kusuma.payment_engine.dto.request.AddBeneficiaryRequest;
import com.kusuma.payment_engine.dto.request.UpdateBeneficiaryRequest;
import com.kusuma.payment_engine.dto.response.BeneficiaryResponse;

public interface BeneficiaryService {

	BeneficiaryResponse addBeneficiary(AddBeneficiaryRequest request);

	List<BeneficiaryResponse> getMyBeneficiaries();

	BeneficiaryResponse getBeneficiary(Long beneficiaryId);

	BeneficiaryResponse updateBeneficiary(Long beneficiaryId, UpdateBeneficiaryRequest request);

	void deleteBeneficiary(Long beneficiaryId);
}