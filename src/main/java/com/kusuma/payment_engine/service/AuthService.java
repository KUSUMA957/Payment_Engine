package com.kusuma.payment_engine.service;

import com.kusuma.payment_engine.dto.request.LoginRequest;
import com.kusuma.payment_engine.dto.request.RegisterRequest;
import com.kusuma.payment_engine.dto.request.ResendOtpRequest;
import com.kusuma.payment_engine.dto.request.VerifyOtpRequest;
import com.kusuma.payment_engine.dto.response.LoginResponse;
import com.kusuma.payment_engine.dto.response.RegisterResponse;

public interface AuthService {

	RegisterResponse register(RegisterRequest request);
	String verifyEmailOtp(VerifyOtpRequest request);
	String resendOtp(ResendOtpRequest request);
	LoginResponse login(LoginRequest request);
}