package com.kusuma.payment_engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(

		@NotBlank(message = "Full name is required") @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters") 
		String fullName,
		@NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits") 
		String phoneNumber) {
}