package com.kusuma.payment_engine.service;

public interface EmailService {

	void sendOtpEmail(String toEmail, String otp);

	void sendEmail(String toEmail, String subject, String body);
}