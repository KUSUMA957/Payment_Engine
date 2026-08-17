package com.kusuma.payment_engine.service;

import java.util.List;

import com.kusuma.payment_engine.dto.response.AdminUserResponse;

public interface AdminService {

	List<AdminUserResponse> getAllUsers();
	AdminUserResponse getUserById(Long userId);
	void lockUser(Long userId);
	void unlockUser(Long userId);
	void disableUser(Long userId);
	void enableUser(Long userId);
}