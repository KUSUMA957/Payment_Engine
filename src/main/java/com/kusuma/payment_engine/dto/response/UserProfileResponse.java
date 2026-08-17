package com.kusuma.payment_engine.dto.response;

import com.kusuma.payment_engine.enums.Role;
import lombok.Builder;

@Builder
public record UserProfileResponse(Long id, String fullName, String email, String phoneNumber, Role role,
		Boolean emailVerified) {
}