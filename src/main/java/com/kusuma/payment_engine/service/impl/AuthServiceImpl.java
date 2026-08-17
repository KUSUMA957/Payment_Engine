package com.kusuma.payment_engine.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kusuma.payment_engine.dto.request.ForgotPasswordRequest;
import com.kusuma.payment_engine.dto.request.LoginRequest;
import com.kusuma.payment_engine.dto.request.RegisterRequest;
import com.kusuma.payment_engine.dto.request.ResendOtpRequest;
import com.kusuma.payment_engine.dto.request.ResetPasswordRequest;
import com.kusuma.payment_engine.dto.request.VerifyOtpRequest;
import com.kusuma.payment_engine.dto.response.LoginResponse;
import com.kusuma.payment_engine.dto.response.RegisterResponse;
import com.kusuma.payment_engine.entity.EmailVerificationOtp;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.OtpType;
import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.enums.UserStatus;
import com.kusuma.payment_engine.exception.EmailNotVerifiedException;
import com.kusuma.payment_engine.exception.InvalidCredentialsException;
import com.kusuma.payment_engine.exception.InvalidOtpException;
import com.kusuma.payment_engine.exception.OtpExpiredException;
import com.kusuma.payment_engine.exception.UserAlreadyExistsException;
import com.kusuma.payment_engine.repository.EmailVerificationOtpRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.security.JwtUtil;
import com.kusuma.payment_engine.service.AuthService;
import com.kusuma.payment_engine.service.EmailService;
import com.kusuma.payment_engine.util.OtpGeneratorUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailVerificationOtpRepository otpRepository;
	private final EmailService emailService;

	@Override
	public RegisterResponse register(RegisterRequest request) {
		String normalizedEmail = request.getEmail().trim().toLowerCase();
		String normalizedFullName = request.getFullName().trim().replaceAll("\\s+", " ");
		String normalizedPhoneNumber = request.getPhoneNumber().trim();
		log.info("User registration initiated. Email={}", normalizedEmail);
		if (userRepository.existsByEmail(normalizedEmail)) {
			throw new UserAlreadyExistsException("User already exists with email : " + normalizedEmail);
		}
		if (userRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
			throw new UserAlreadyExistsException("User already exists with phone number : " + normalizedPhoneNumber);
		}
		User user = User.builder().fullName(normalizedFullName).email(normalizedEmail)
				.phoneNumber(normalizedPhoneNumber).password(passwordEncoder.encode(request.getPassword()))
				.role(Role.CUSTOMER).status(UserStatus.ACTIVE).lastPasswordChangedAt(LocalDateTime.now()).build();
		User savedUser = userRepository.save(user);
		String otp = OtpGeneratorUtil.generateOtp();
		EmailVerificationOtp otpEntity = EmailVerificationOtp.builder().email(savedUser.getEmail()).otp(otp)
				.otpType(OtpType.EMAIL_VERIFICATION).expiresAt(LocalDateTime.now().plusMinutes(5))
				.lastSentAt(LocalDateTime.now()).used(false).build();
		otpRepository.save(otpEntity);
		emailService.sendOtpEmail(savedUser.getEmail(), otp);
		return RegisterResponse.builder().userId(savedUser.getId()).fullName(savedUser.getFullName())
				.phoneNumber(savedUser.getPhoneNumber()).email(savedUser.getEmail())
				.message("User Registered Successfully").build();
	}

	@Override
	@Transactional(noRollbackFor = InvalidCredentialsException.class)
	public LoginResponse login(LoginRequest request) {
		String normalizedEmail = request.getEmail().trim().toLowerCase();
		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
		if (!Boolean.TRUE.equals(user.getEmailVerified())) {
			throw new EmailNotVerifiedException("Please verify your email first.");
		}
		validateAccountStatus(user);
		if (user.getAccountLockedUntil() != null && !user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
			user.setFailedLoginAttempts(0);
			user.setAccountLockedUntil(null);
			userRepository.save(user);
			log.info("Account lock expired. Resetting failed attempts. Email={}", normalizedEmail);
		}
		if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
			throw new InvalidCredentialsException("Account is locked. Try again later.");
		}
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			int attempts = user.getFailedLoginAttempts() + 1;
			user.setFailedLoginAttempts(attempts);
			if (attempts >= 5) {
				user.setFailedLoginAttempts(5);
				user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
				log.warn("User account locked. Email={}", normalizedEmail);
			}
			userRepository.save(user);
			throw new InvalidCredentialsException("Invalid email or password");
		}
		user.setFailedLoginAttempts(0);
		user.setAccountLockedUntil(null);
		user.setLastLoginAt(LocalDateTime.now());
		userRepository.save(user);
		String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
		return LoginResponse.builder().userId(user.getId()).email(user.getEmail()).role(user.getRole().name())
				.token(token).message("Login Successful").build();
	}

	@Override
	public String resendOtp(ResendOtpRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidOtpException("User not found"));
		validateAccountStatus(user);
		if (Boolean.TRUE.equals(user.getEmailVerified())) {
			throw new InvalidOtpException("Email already verified.");
		}
		EmailVerificationOtp latestOtp = otpRepository
				.findTopByEmailAndOtpTypeOrderByCreatedAtDesc(email, OtpType.EMAIL_VERIFICATION).orElse(null);
		if (latestOtp != null && latestOtp.getLastSentAt() != null
				&& latestOtp.getLastSentAt().plusMinutes(1).isAfter(LocalDateTime.now())) {
			throw new InvalidOtpException("Please wait 1 minute before requesting another OTP.");
		}
		String otp = OtpGeneratorUtil.generateOtp();
		EmailVerificationOtp otpEntity = EmailVerificationOtp.builder().email(email).otp(otp)
				.otpType(OtpType.EMAIL_VERIFICATION).expiresAt(LocalDateTime.now().plusMinutes(5))
				.lastSentAt(LocalDateTime.now()).used(false).build();
		otpRepository.save(otpEntity);
		emailService.sendOtpEmail(email, otp);
		log.info("New verification OTP sent successfully. Email={}", email);
		return "OTP sent successfully";
	}

	@Override
	@Transactional(noRollbackFor = InvalidOtpException.class)
	public String verifyEmailOtp(VerifyOtpRequest request) {
		String normalizedEmail = request.getEmail().trim().toLowerCase();
		EmailVerificationOtp otpRecord = otpRepository
				.findTopByEmailAndOtpTypeOrderByCreatedAtDesc(normalizedEmail, OtpType.EMAIL_VERIFICATION)
				.orElseThrow(() -> new InvalidOtpException("Email verification OTP not found"));
		if (Boolean.TRUE.equals(otpRecord.getLocked())) {
			log.warn("OTP verification attempted on locked OTP. Email={}", normalizedEmail);
			throw new InvalidOtpException("OTP verification locked. Please request a new OTP.");
		}
		if (Boolean.TRUE.equals(otpRecord.getUsed())) {
			log.warn("OTP verification failed. OTP already used. Email={}", normalizedEmail);
			throw new InvalidOtpException("OTP already used");
		}
		if (!otpRecord.getOtp().equals(request.getOtp())) {
			int attempts = otpRecord.getFailedAttempts() + 1;
			otpRecord.setFailedAttempts(attempts);
			log.info("Failed OTP attempt count : {}", otpRecord.getFailedAttempts());
			if (attempts >= 5) {
				otpRecord.setFailedAttempts(5);
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
		validateAccountStatus(user);
		user.setEmailVerified(true);
		otpRecord.setUsed(true);
		userRepository.save(user);
		otpRepository.save(otpRecord);
		log.info("Email verified successfully. Email={}", normalizedEmail);
		return "Email verified successfully";
	}

	@Override
	public String forgotPassword(ForgotPasswordRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new InvalidCredentialsException("User not found"));
		validateAccountStatus(user);
		if (!Boolean.TRUE.equals(user.getEmailVerified())) {
			throw new EmailNotVerifiedException("Please verify your email first.");
		}
		EmailVerificationOtp latestOtp = otpRepository
				.findTopByEmailAndOtpTypeOrderByCreatedAtDesc(email, OtpType.PASSWORD_RESET).orElse(null);
		if (latestOtp != null && latestOtp.getLastSentAt() != null
				&& latestOtp.getLastSentAt().plusMinutes(1).isAfter(LocalDateTime.now())) {
			throw new InvalidOtpException("Please wait 1 minute before requesting another OTP.");
		}
		String otp = OtpGeneratorUtil.generateOtp();
		EmailVerificationOtp otpEntity = EmailVerificationOtp.builder().email(email).otp(otp)
				.otpType(OtpType.PASSWORD_RESET).expiresAt(LocalDateTime.now().plusMinutes(5))
				.lastSentAt(LocalDateTime.now()).used(false).build();
		otpRepository.save(otpEntity);
		emailService.sendOtpEmail(email, otp);
		log.info("Password reset OTP sent successfully. Email={}", email);
		return "Password reset OTP sent successfully";
	}

	@Override
	@Transactional(noRollbackFor = InvalidOtpException.class)
	public String resetPassword(ResetPasswordRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new InvalidCredentialsException("User not found"));
		validateAccountStatus(user);
		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("New password must be different from current password.");
		}
		EmailVerificationOtp otpRecord = otpRepository
				.findTopByEmailAndOtpTypeOrderByCreatedAtDesc(email, OtpType.PASSWORD_RESET)
				.orElseThrow(() -> new InvalidOtpException("Password reset OTP not found"));
		if (Boolean.TRUE.equals(otpRecord.getLocked())) {
			throw new InvalidOtpException("OTP locked. Request a new OTP.");
		}
		if (Boolean.TRUE.equals(otpRecord.getUsed())) {
			throw new InvalidOtpException("OTP already used");
		}
		if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new OtpExpiredException("OTP expired");
		}
		if (!otpRecord.getOtp().equals(request.getOtp())) {
			int attempts = otpRecord.getFailedAttempts() + 1;
			otpRecord.setFailedAttempts(attempts);
			if (attempts >= 5) {
				otpRecord.setFailedAttempts(5);
				otpRecord.setLocked(true);
			}
			otpRepository.save(otpRecord);
			throw new InvalidOtpException("Invalid OTP");
		}
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		user.setLastPasswordChangedAt(LocalDateTime.now());
		user.setFailedLoginAttempts(0);
		user.setAccountLockedUntil(null);
		otpRecord.setUsed(true);
		userRepository.save(user);
		otpRepository.save(otpRecord);
		return "Password reset successful";
	}

	private void validateAccountStatus(User user) {
		if (user.getStatus() == UserStatus.LOCKED) {
			throw new InvalidCredentialsException("Account is locked by administrator.");
		}
		if (user.getStatus() == UserStatus.INACTIVE) {
			throw new InvalidCredentialsException("Account is inactive.");
		}
	}
}