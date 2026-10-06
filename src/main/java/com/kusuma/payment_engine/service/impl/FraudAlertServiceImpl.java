package com.kusuma.payment_engine.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.dto.response.FraudAlertActionResponse;
import com.kusuma.payment_engine.dto.response.HighRiskUserResponse;
import com.kusuma.payment_engine.entity.FraudAlert;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.RiskLevel;
import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.exception.InvalidTransactionException;
import com.kusuma.payment_engine.exception.UnauthorizedTransactionAccessException;
import com.kusuma.payment_engine.repository.FraudAlertRepository;
import com.kusuma.payment_engine.service.CurrentUserService;
import com.kusuma.payment_engine.service.FraudAlertService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudAlertServiceImpl implements FraudAlertService {

	private final FraudAlertRepository fraudAlertRepository;
	private final CurrentUserService currentUserService;

	@Override
	@Transactional
	public FraudAlertActionResponse resolveAlert(Long alertId) {
		User admin = currentUserService.getAuthenticatedUser();
		if (admin.getRole() != Role.ADMIN) {
			throw new UnauthorizedTransactionAccessException("Admin access required");
		}
		FraudAlert alert = fraudAlertRepository.findById(alertId)
				.orElseThrow(() -> new InvalidTransactionException("Fraud alert not found"));
		if (alert.isResolved()) {
			throw new InvalidTransactionException("Fraud alert already resolved");
		}
		alert.setResolved(true);
		fraudAlertRepository.save(alert);
		return FraudAlertActionResponse.builder().message("Fraud alert resolved successfully").build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<HighRiskUserResponse> getHighRiskUsers() {
		validateAdmin();
		return fraudAlertRepository.findUsersWithActiveAlerts().stream().map(user -> {
			Integer riskScore = fraudAlertRepository.getUserRiskScore(user);
			return HighRiskUserResponse.builder().userId(user.getId()).email(user.getEmail()).riskScore(riskScore)
					.riskLevel(calculateRiskLevel(riskScore)).build();
		}).sorted((u1, u2) -> Integer.compare(u2.riskScore(), u1.riskScore())).toList();
	}

	private RiskLevel calculateRiskLevel(Integer score) {
		if (score >= 81) {
			return RiskLevel.CRITICAL;
		}
		if (score >= 51) {
			return RiskLevel.HIGH;
		}
		if (score >= 21) {
			return RiskLevel.MEDIUM;
		}
		return RiskLevel.LOW;
	}

	private void validateAdmin() {
		User admin = currentUserService.getAuthenticatedUser();
		if (admin.getRole() != Role.ADMIN) {
			throw new UnauthorizedTransactionAccessException("Admin access required");
		}
	}
}