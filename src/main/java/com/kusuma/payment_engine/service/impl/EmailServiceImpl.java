package com.kusuma.payment_engine.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.exception.EmailDeliveryException;
import com.kusuma.payment_engine.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
	private final JavaMailSender mailSender;

	@Override
	public void sendOtpEmail(String toEmail, String otp) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("mogadalakusuma@gmail.com");
			message.setTo(toEmail);
			message.setSubject("Payment Engine Email Verification");
			message.setText("Your OTP is: " + otp + "\n\nValid for 5 minutes.");
			mailSender.send(message);
			log.info("OTP email sent successfully to {}", toEmail);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Email delivery failed for {}", toEmail, ex);
			throw new EmailDeliveryException("Failed to send OTP email.", ex);
		}
	}
}