package com.kusuma.payment_engine.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.dto.request.LoginRequest;
import com.kusuma.payment_engine.dto.request.RegisterRequest;
import com.kusuma.payment_engine.dto.request.ResendOtpRequest;
import com.kusuma.payment_engine.dto.request.VerifyOtpRequest;
import com.kusuma.payment_engine.dto.response.LoginResponse;
import com.kusuma.payment_engine.dto.response.RegisterResponse;
import com.kusuma.payment_engine.entity.EmailVerificationOtp;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.enums.UserStatus;
import com.kusuma.payment_engine.exception.EmailNotVerifiedException;
import com.kusuma.payment_engine.exception.InvalidCredentialsException;
import com.kusuma.payment_engine.exception.InvalidOtpException;
import com.kusuma.payment_engine.exception.OtpExpiredException;
import com.kusuma.payment_engine.exception.UserAlreadyExistsException;
import com.kusuma.payment_engine.repository.EmailVerificationOtpRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AuthService;
import com.kusuma.payment_engine.service.EmailService;
import com.kusuma.payment_engine.util.OtpGeneratorUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailVerificationOtpRepository otpRepository;
	private final EmailService emailService;

	@Override
	public RegisterResponse register(RegisterRequest request) {
		String normalizedEmail = request.getEmail().trim().toLowerCase();
		String normalizedFullName = request.getFullName().trim().replaceAll("\\s+", " ");
		// String phoneNumber = request.getPhoneNumber();
		String normalizedPhoneNumber = request.getPhoneNumber().trim();
		log.info("User registration initiated. Email={}", normalizedEmail);
		if (userRepository.existsByEmail(normalizedEmail)) {
			log.warn("Registration failed. Duplicate email={}", normalizedEmail);
			throw new UserAlreadyExistsException("User already exists with email : " + normalizedEmail);
		}
		if (userRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
			throw new UserAlreadyExistsException("User already exists with phone number : " + request.getPhoneNumber());
		}
		User user = User.builder().fullName(normalizedFullName).email(normalizedEmail).phoneNumber(normalizedPhoneNumber)
				.password(passwordEncoder.encode(request.getPassword())).role(Role.CUSTOMER).status(UserStatus.ACTIVE)
				.build();
		User savedUser = userRepository.save(user);
		String otp = OtpGeneratorUtil.generateOtp();
		EmailVerificationOtp otpEntity = EmailVerificationOtp.builder().email(savedUser.getEmail()).otp(otp)
				.expiresAt(LocalDateTime.now().plusMinutes(5)).lastSentAt(LocalDateTime.now()).used(false).build();
		otpRepository.save(otpEntity);
		emailService.sendOtpEmail(savedUser.getEmail(), otp);
		log.info("OTP generated and sent. Email={}", savedUser.getEmail());
		log.info("User registration completed successfully. UserId={}, Email={}", savedUser.getId(),
				savedUser.getEmail());
		return RegisterResponse.builder().userId(savedUser.getId()).fullName(savedUser.getFullName())
				.phoneNumber(normalizedPhoneNumber).email(savedUser.getEmail()).message("User Registered Successfully")
				.build();
	}

	@Override
	public String resendOtp(ResendOtpRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidOtpException("User not found"));
		if (Boolean.TRUE.equals(user.getEmailVerified())) {
			throw new InvalidOtpException("Email already verified.");
		}
		EmailVerificationOtp latestOtp = otpRepository.findTopByEmailOrderByCreatedAtDesc(email).orElse(null);
		if (latestOtp != null && latestOtp.getLastSentAt() != null
				&& latestOtp.getLastSentAt().plusMinutes(1).isAfter(LocalDateTime.now())) {
			throw new InvalidOtpException("Please wait 1 minute before requesting another OTP.");
		}
		String otp = OtpGeneratorUtil.generateOtp();
		EmailVerificationOtp otpEntity = EmailVerificationOtp.builder().email(email).otp(otp)
				.expiresAt(LocalDateTime.now().plusMinutes(5)).lastSentAt(LocalDateTime.now()).used(false).build();
		otpRepository.save(otpEntity);
		emailService.sendOtpEmail(email, otp);
		log.info("New OTP sent successfully. Email={}", email);
		return "OTP sent successfully";
	}

	@Override
	@Transactional(noRollbackFor = InvalidOtpException.class)
	public String verifyEmailOtp(VerifyOtpRequest request) {
		String normalizedEmail = request.getEmail().trim().toLowerCase();
		EmailVerificationOtp otpRecord = otpRepository.findTopByEmailOrderByCreatedAtDesc(normalizedEmail)
				.orElseThrow(() -> new InvalidOtpException("OTP not found for email: " + normalizedEmail));
		if (Boolean.TRUE.equals(otpRecord.getLocked())) {
			log.warn("OTP verification attempted on locked OTP. Email={}", normalizedEmail);
			throw new InvalidOtpException("OTP verification locked. Please request a new OTP.");
		}
		if (Boolean.TRUE.equals(otpRecord.getUsed())) {
			log.warn("OTP verification failed. OTP already used. Email={}", normalizedEmail);
			throw new InvalidOtpException("OTP already used");
		}
		if (!otpRecord.getOtp().equals(request.getOtp())) {
			log.info("Before increment : {}", otpRecord.getFailedAttempts());
			otpRecord.setFailedAttempts(otpRecord.getFailedAttempts() + 1);
			log.info("After increment : {}", otpRecord.getFailedAttempts());
			if (otpRecord.getFailedAttempts() >= 5) {
				otpRecord.setLocked(true);
				log.warn("OTP locked after maximum failed attempts. Email={}", normalizedEmail);
			}
			otpRepository.save(otpRecord);
			throw new InvalidOtpException("Invalid OTP");
		}
		if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
			log.warn("OTP verification failed. OTP expired. Email={}", normalizedEmail);
			throw new OtpExpiredException("OTP expired");
		}
		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new InvalidOtpException("User not found"));
		user.setEmailVerified(true);
		otpRecord.setUsed(true);
		userRepository.save(user);
		otpRepository.save(otpRecord);
		log.info("Email verified successfully. Email={}", normalizedEmail);
		return "Email verified successfully";
	}

	@Override
	@Transactional(noRollbackFor = InvalidCredentialsException.class)
	public LoginResponse login(LoginRequest request) {
		String normalizedEmail = request.getEmail().trim().toLowerCase();
		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
		if (!Boolean.TRUE.equals(user.getEmailVerified())) {
			log.warn("Login failed. Email not verified. Email={}", normalizedEmail);
			throw new EmailNotVerifiedException("Please verify your email first.");
		}
		if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
			log.warn("Login attempted on locked account. Email={}", normalizedEmail);
			throw new InvalidCredentialsException("Account is locked. Try again later.");
		}
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
			log.warn("Invalid password attempt {} for Email={}", user.getFailedLoginAttempts(), normalizedEmail);
			if (user.getFailedLoginAttempts() >= 5) {
				user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
				log.warn("User account locked for 30 minutes. Email={}", normalizedEmail);
			}
			userRepository.save(user);
			throw new InvalidCredentialsException("Invalid email or password");
		}
		user.setFailedLoginAttempts(0);
		user.setAccountLockedUntil(null);
		user.setLastLoginAt(LocalDateTime.now());
		userRepository.save(user);
		log.info("User logged in successfully. UserId={}, Email={}", user.getId(), user.getEmail());
		return LoginResponse.builder().userId(user.getId()).email(user.getEmail()).role(user.getRole().name())
				.message("Login Successful").build();
	}
}