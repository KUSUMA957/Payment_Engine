package com.kusuma.payment_engine.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.dto.request.ChangePasswordRequest;
import com.kusuma.payment_engine.dto.request.UpdateUserProfileRequest;
import com.kusuma.payment_engine.dto.response.UserProfileResponse;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;
import com.kusuma.payment_engine.enums.NotificationType;
import com.kusuma.payment_engine.exception.InvalidCurrentPasswordException;
import com.kusuma.payment_engine.exception.NoChangesDetectedException;
import com.kusuma.payment_engine.exception.PasswordMismatchException;
import com.kusuma.payment_engine.exception.PhoneNumberAlreadyExistsException;
import com.kusuma.payment_engine.exception.SamePasswordException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AuditLogService;
import com.kusuma.payment_engine.service.EmailService;
import com.kusuma.payment_engine.service.NotificationService;
import com.kusuma.payment_engine.service.UserService;
import com.kusuma.payment_engine.util.AccountValidationUtil;
import com.kusuma.payment_engine.util.EmailTemplateUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuditLogService auditLogService;
	private final NotificationService notificationService;
	private final EmailService emailService;
	@Override
	public UserProfileResponse getCurrentUser() {
		User user = getAuthenticatedUser();
		return mapToUserProfileResponse(user);
	}

	@Override
	public UserProfileResponse updateCurrentUser(UpdateUserProfileRequest request) {
		User user = getAuthenticatedUser();
		String fullName = normalizeFullName(request.fullName());
		String phoneNumber = request.phoneNumber().trim();
		boolean sameName = user.getFullName().equals(fullName);
		boolean samePhone = user.getPhoneNumber().equals(phoneNumber);
		if (sameName && samePhone) {
			throw new NoChangesDetectedException("No changes detected");
		}
		boolean phoneChanged = !user.getPhoneNumber().equals(phoneNumber);
		if (phoneChanged && userRepository.existsByPhoneNumber(phoneNumber)) {
			throw new PhoneNumberAlreadyExistsException("Phone number already exists");
		}
		user.setFullName(fullName);
		user.setPhoneNumber(phoneNumber);
		User updatedUser = userRepository.save(user);
		auditLogService.log(user.getEmail(), AuditAction.UPDATE_PROFILE, AuditEntityType.USER, user.getId(),
				"Profile updated");
		notificationService.createNotification(user, "Profile Updated",
				"Your profile details were updated successfully.", NotificationType.SECURITY, false);
		return mapToUserProfileResponse(updatedUser);
	}

	@Override
	public void changePassword(ChangePasswordRequest request) {
		User user = getAuthenticatedUser();
		if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
			throw new InvalidCurrentPasswordException("Current password is incorrect");
		}
		if (!request.newPassword().equals(request.confirmPassword())) {
			throw new PasswordMismatchException("New password and confirm password do not match");
		}
		if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
			throw new SamePasswordException("New password must be different from current password");
		}
		user.setPassword(passwordEncoder.encode(request.newPassword()));
		user.setLastPasswordChangedAt(LocalDateTime.now());
		user.setFailedLoginAttempts(0);
		user.setAccountLockedUntil(null);
		userRepository.save(user);
		auditLogService.log(user.getEmail(), AuditAction.CHANGE_PASSWORD, AuditEntityType.USER, user.getId(),
				"Password changed");
		notificationService.createNotification(user, "Password Changed", "Your password has been changed successfully.",
				NotificationType.SECURITY, false);
		String emailBody = EmailTemplateUtil.passwordChangedEmail(user.getFullName());
		emailService.sendEmail(user.getEmail(), "Payment Engine - Password Changed", emailBody);
	}

	private User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
		AccountValidationUtil.validateUserStatus(user);
		return user;
	}

	private UserProfileResponse mapToUserProfileResponse(User user) {
		return UserProfileResponse.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.phoneNumber(user.getPhoneNumber()).role(user.getRole()).emailVerified(user.getEmailVerified()).build();
	}

	private String normalizeFullName(String fullName) {
		return fullName.trim().replaceAll("\\s+", " ");
	}
}
