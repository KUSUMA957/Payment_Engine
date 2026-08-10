package com.kusuma.payment_engine.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.dto.request.RegisterRequest;
import com.kusuma.payment_engine.dto.response.RegisterResponse;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.enums.UserStatus;
import com.kusuma.payment_engine.exception.UserAlreadyExistsException;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public RegisterResponse register(RegisterRequest request) {

		String normalizedEmail = request.getEmail().trim().toLowerCase();
		String normalizedFullName = request.getFullName().trim();
		log.info("User registration initiated. Email={}", normalizedEmail);
		if (userRepository.existsByEmail(normalizedEmail)) {
			log.warn("Registration failed. Duplicate email={}", normalizedEmail);
			throw new UserAlreadyExistsException("User already exists with email : " + normalizedEmail);
		}
		User user = User.builder().fullName(normalizedFullName).email(normalizedEmail)
				.password(passwordEncoder.encode(request.getPassword())).role(Role.CUSTOMER).status(UserStatus.ACTIVE)
				.build();
		User savedUser = userRepository.save(user);
		log.info("User registration completed successfully. UserId={}, Email={}", savedUser.getId(),
				savedUser.getEmail());
		return RegisterResponse.builder().userId(savedUser.getId()).fullName(savedUser.getFullName())
				.email(savedUser.getEmail()).message("User Registered Successfully").build();
	}
}
