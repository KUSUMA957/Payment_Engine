package com.kusuma.payment_engine.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.dto.response.AdminUserResponse;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.UserStatus;
import com.kusuma.payment_engine.exception.SelfAdminActionException;
import com.kusuma.payment_engine.exception.UserAlreadyDisabledException;
import com.kusuma.payment_engine.exception.UserAlreadyEnabledException;
import com.kusuma.payment_engine.exception.UserAlreadyLockedException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.exception.UserNotLockedException;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;

	@Override
	public List<AdminUserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	public AdminUserResponse getUserById(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		return mapToResponse(user);
	}

	private AdminUserResponse mapToResponse(User user) {
		return AdminUserResponse.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.phoneNumber(user.getPhoneNumber()).role(user.getRole()).status(user.getStatus())
				.emailVerified(user.getEmailVerified()).build();
	}

	@Override
	public void lockUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		String currentAdmin = getCurrentAdminEmail();
		if (user.getEmail().equals(currentAdmin)) {
			throw new SelfAdminActionException("Admin cannot lock own account");
		}
		if (user.getStatus() == UserStatus.INACTIVE) {
			throw new UserAlreadyDisabledException("Cannot lock a disabled account");
		}
		if (user.getStatus() == UserStatus.LOCKED) {
			throw new UserAlreadyLockedException("User already locked");
		}
		user.setStatus(UserStatus.LOCKED);
		user.setLastPasswordChangedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	@Override
	public void unlockUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		if (user.getStatus() != UserStatus.LOCKED) {
			throw new UserNotLockedException("User is not locked");
		}
		user.setStatus(UserStatus.ACTIVE);
		user.setFailedLoginAttempts(0);
		user.setAccountLockedUntil(null);
		user.setLastPasswordChangedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	@Override
	public void disableUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		String currentAdmin = getCurrentAdminEmail();
		if (user.getEmail().equals(currentAdmin)) {
			throw new SelfAdminActionException("Admin cannot disable own account");
		}
		if (user.getStatus() == UserStatus.INACTIVE) {
			throw new UserAlreadyDisabledException("User already disabled");
		}
		user.setStatus(UserStatus.INACTIVE);
		user.setLastPasswordChangedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	@Override
	public void enableUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		if (user.getStatus() == UserStatus.ACTIVE) {
			throw new UserAlreadyEnabledException("User already active");
		}
		user.setStatus(UserStatus.ACTIVE);
		user.setFailedLoginAttempts(0);
		user.setAccountLockedUntil(null);
		user.setLastPasswordChangedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	private String getCurrentAdminEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}