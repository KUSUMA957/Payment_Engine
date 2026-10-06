package com.kusuma.payment_engine.service;

import java.util.List;

import com.kusuma.payment_engine.dto.response.FraudAlertActionResponse;
import com.kusuma.payment_engine.dto.response.HighRiskUserResponse;

public interface FraudAlertService {

	FraudAlertActionResponse resolveAlert(Long alertId);

	List<HighRiskUserResponse> getHighRiskUsers();
}