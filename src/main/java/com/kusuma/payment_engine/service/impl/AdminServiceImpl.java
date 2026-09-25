package com.kusuma.payment_engine.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.dto.response.AdminUserResponse;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.AuditAction;
import com.kusuma.payment_engine.enums.AuditEntityType;
import com.kusuma.payment_engine.enums.NotificationType;
import com.kusuma.payment_engine.enums.UserStatus;
import com.kusuma.payment_engine.exception.SelfAdminActionException;
import com.kusuma.payment_engine.exception.UserAlreadyDisabledException;
import com.kusuma.payment_engine.exception.UserAlreadyEnabledException;
import com.kusuma.payment_engine.exception.UserAlreadyLockedException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.exception.UserNotLockedException;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AdminService;
import com.kusuma.payment_engine.service.AuditLogService;
import com.kusuma.payment_engine.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;
	private final AuditLogService auditLogService;
	private final NotificationService notificationService;

	@Override
	public List<AdminUserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	public AdminUserResponse getUserById(Long userId) {
		return mapToResponse(getUserByIdOrThrow(userId));
	}

	@Override
	public void lockUser(Long userId) {
		User user = getUserByIdOrThrow(userId);
		validateNotSelfAction(user);
		if (user.getStatus() == UserStatus.INACTIVE) {
			throw new UserAlreadyDisabledException("Cannot lock a disabled account");
		}
		if (user.getStatus() == UserStatus.LOCKED) {
			throw new UserAlreadyLockedException("User already locked");
		}
		updateUserStatus(user, UserStatus.LOCKED);
		auditLogService.log(getCurrentAdminEmail(), AuditAction.LOCK_USER, AuditEntityType.USER, user.getId(),
				"Locked user account: " + user.getEmail());
		notificationService.createNotification(user, "Account Locked", "Your account has been locked by administrator.",
				NotificationType.ADMIN);
	}

	@Override
	public void unlockUser(Long userId) {
		User user = getUserByIdOrThrow(userId);
		if (user.getStatus() != UserStatus.LOCKED) {
			throw new UserNotLockedException("User is not locked");
		}
		user.setStatus(UserStatus.ACTIVE);
		user.setFailedLoginAttempts(0);
		user.setAccountLockedUntil(null);
		userRepository.save(user);
		auditLogService.log(getCurrentAdminEmail(), AuditAction.UNLOCK_USER, AuditEntityType.USER, user.getId(),
				"Unlocked user account: " + user.getEmail());
		notificationService.createNotification(user, "Account Unlocked", "Your account has been unlocked.",
				NotificationType.ADMIN);
	}

	@Override
	public void disableUser(Long userId) {
		User user = getUserByIdOrThrow(userId);
		validateNotSelfAction(user);
		if (user.getStatus() == UserStatus.INACTIVE) {
			throw new UserAlreadyDisabledException("User already disabled");
		}
		updateUserStatus(user, UserStatus.INACTIVE);
		auditLogService.log(getCurrentAdminEmail(), AuditAction.DISABLE_USER, AuditEntityType.USER, user.getId(),
				"Disabled user account: " + user.getEmail());
		notificationService.createNotification(user, "Account Disabled",
				"Your account has been disabled by administrator.", NotificationType.ADMIN);
	}

	@Override
	public void enableUser(Long userId) {
		User user = getUserByIdOrThrow(userId);
		if (user.getStatus() == UserStatus.ACTIVE) {
			throw new UserAlreadyEnabledException("User already active");
		}
		user.setStatus(UserStatus.ACTIVE);
		user.setFailedLoginAttempts(0);
		user.setAccountLockedUntil(null);
		userRepository.save(user);
		auditLogService.log(getCurrentAdminEmail(), AuditAction.ENABLE_USER, AuditEntityType.USER, user.getId(),
				"Enabled user account: " + user.getEmail());
		notificationService.createNotification(user, "Account Enabled", "Your account has been enabled again.",
				NotificationType.ADMIN);
	}

	private User getUserByIdOrThrow(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private void validateNotSelfAction(User user) {
		String currentAdmin = getCurrentAdminEmail();
		if (user.getEmail().equalsIgnoreCase(currentAdmin)) {
			throw new SelfAdminActionException("Admin cannot perform action on own account");
		}
	}

	private String getCurrentAdminEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private void updateUserStatus(User user, UserStatus status) {
		user.setStatus(status);
		userRepository.save(user);
	}

	private AdminUserResponse mapToResponse(User user) {
		return AdminUserResponse.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.phoneNumber(user.getPhoneNumber()).role(user.getRole()).status(user.getStatus())
				.emailVerified(user.getEmailVerified()).build();
	}
}