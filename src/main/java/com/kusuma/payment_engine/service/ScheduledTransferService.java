package com.kusuma.payment_engine.service;

import java.util.List;

import com.kusuma.payment_engine.dto.request.CreateScheduledTransferRequest;
import com.kusuma.payment_engine.dto.response.ScheduledTransferResponse;

public interface ScheduledTransferService {

	ScheduledTransferResponse scheduleTransfer(CreateScheduledTransferRequest request);

	List<ScheduledTransferResponse> getMyScheduledTransfers();

	ScheduledTransferResponse getScheduledTransfer(Long transferId);

	void cancelScheduledTransfer(Long transferId);
}