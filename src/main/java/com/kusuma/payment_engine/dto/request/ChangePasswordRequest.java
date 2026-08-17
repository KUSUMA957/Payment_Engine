package com.kusuma.payment_engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangePasswordRequest(

		@NotBlank(message = "Current password is required") 
		String currentPassword,
		@NotBlank(message = "New password is required")
		@Size(min = 8, max = 20,message = "Password must be between 8 and 20 characters")
		@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "Password must contain uppercase, lowercase, digit and special character")
		String newPassword,
		@NotBlank(message = "Confirm password is required") 
		String confirmPassword) {
}