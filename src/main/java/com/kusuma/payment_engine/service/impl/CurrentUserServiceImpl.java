package com.kusuma.payment_engine.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

	private final UserRepository userRepository;

	@Override
	public User getAuthenticatedUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
	}
}