package com.kusuma.payment_engine.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);
}