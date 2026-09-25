package com.kusuma.payment_engine.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.kusuma.payment_engine.dto.request.AddBeneficiaryRequest;
import com.kusuma.payment_engine.dto.request.UpdateBeneficiaryRequest;
import com.kusuma.payment_engine.dto.response.BeneficiaryResponse;
import com.kusuma.payment_engine.service.BeneficiaryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

	private final BeneficiaryService beneficiaryService;

	@PostMapping
	public BeneficiaryResponse addBeneficiary(@Valid @RequestBody AddBeneficiaryRequest request) {
		return beneficiaryService.addBeneficiary(request);
	}

	@GetMapping
	public List<BeneficiaryResponse> getMyBeneficiaries() {
		return beneficiaryService.getMyBeneficiaries();
	}

	@GetMapping("/{id}")
	public BeneficiaryResponse getBeneficiary(@PathVariable("id") Long id) {
		return beneficiaryService.getBeneficiary(id);
	}

	@PutMapping("/{id}")
	public BeneficiaryResponse updateBeneficiary(@PathVariable("id") Long id,
			@Valid @RequestBody UpdateBeneficiaryRequest request) {
		return beneficiaryService.updateBeneficiary(id, request);
	}

	@DeleteMapping("/{id}")
	public void deleteBeneficiary(@PathVariable("id") Long id) {
		beneficiaryService.deleteBeneficiary(id);
	}

	@GetMapping("/admin/users/{userId}")
	public List<BeneficiaryResponse> getUserBeneficiaries(@PathVariable("userId") Long userId) {
		return beneficiaryService.getUserBeneficiaries(userId);
	}
}