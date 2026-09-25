package com.kusuma.payment_engine.service;

import java.util.List;

import com.kusuma.payment_engine.dto.response.NotificationResponse;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.NotificationType;

public interface NotificationService {

	void createNotification(User user, String title, String message, NotificationType type, boolean sendEmail);

	List<NotificationResponse> getMyNotifications();

	List<NotificationResponse> getUnreadNotifications();

	void markAsRead(Long notificationId);

	void markAllAsRead();
}