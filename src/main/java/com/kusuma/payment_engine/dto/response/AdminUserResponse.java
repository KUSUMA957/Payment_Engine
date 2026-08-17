package com.kusuma.payment_engine.dto.response;

import com.kusuma.payment_engine.enums.Role;
import com.kusuma.payment_engine.enums.UserStatus;

import lombok.Builder;

@Builder
public record AdminUserResponse(Long id, String fullName, String email, String phoneNumber, Role role,
		UserStatus status, Boolean emailVerified) {
}
