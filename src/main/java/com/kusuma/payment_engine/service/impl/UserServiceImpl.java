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
import com.kusuma.payment_engine.enums.UserStatus;
import com.kusuma.payment_engine.exception.InvalidCredentialsException;
import com.kusuma.payment_engine.exception.InvalidCurrentPasswordException;
import com.kusuma.payment_engine.exception.NoChangesDetectedException;
import com.kusuma.payment_engine.exception.PasswordMismatchException;
import com.kusuma.payment_engine.exception.PhoneNumberAlreadyExistsException;
import com.kusuma.payment_engine.exception.SamePasswordException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserProfileResponse getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
		validateAccountStatus(user);
		return UserProfileResponse.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.phoneNumber(user.getPhoneNumber()).role(user.getRole()).emailVerified(user.getEmailVerified()).build();
	}

	@Override
	public UserProfileResponse updateCurrentUser(UpdateUserProfileRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
		validateAccountStatus(user);
		String fullName = request.fullName().trim();
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
		return UserProfileResponse.builder().id(updatedUser.getId()).fullName(updatedUser.getFullName())
				.email(updatedUser.getEmail()).phoneNumber(updatedUser.getPhoneNumber()).role(updatedUser.getRole())
				.build();
	}

	@Override
	public void changePassword(ChangePasswordRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
		validateAccountStatus(user);
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
		userRepository.save(user);
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
