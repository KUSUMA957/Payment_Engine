package com.kusuma.payment_engine.service;

import com.kusuma.payment_engine.dto.request.ChangePasswordRequest;
import com.kusuma.payment_engine.dto.request.UpdateUserProfileRequest;
import com.kusuma.payment_engine.dto.response.UserProfileResponse;

public interface UserService {
	UserProfileResponse getCurrentUser();
	UserProfileResponse updateCurrentUser(UpdateUserProfileRequest request);
	void changePassword(ChangePasswordRequest request);
}
