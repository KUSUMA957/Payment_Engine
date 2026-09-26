package com.kusuma.payment_engine.service.impl;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kusuma.payment_engine.dto.response.NotificationResponse;
import com.kusuma.payment_engine.entity.Notification;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.exception.NotificationNotFoundException;
import com.kusuma.payment_engine.exception.UserNotFoundException;
import com.kusuma.payment_engine.repository.NotificationRepository;
import com.kusuma.payment_engine.repository.UserRepository;
import com.kusuma.payment_engine.service.EmailService;
import com.kusuma.payment_engine.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final EmailService emailService;

	@Override
	public void createNotification(User user, String title, String message,
			com.kusuma.payment_engine.enums.NotificationType type, boolean sendEmail) {
		Notification notification = Notification.builder().user(user).title(title).message(message)
				.notificationType(type).build();
		notificationRepository.save(notification);
		if (sendEmail) {
			sendNotificationEmail(user.getEmail(), title, message);
		}
	}

	@Override
	public List<NotificationResponse> getMyNotifications() {
		User user = getAuthenticatedUser();
		return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream().map(this::mapToResponse).toList();
	}

	@Override
	public List<NotificationResponse> getUnreadNotifications() {
		User user = getAuthenticatedUser();
		return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user).stream()
				.map(this::mapToResponse).toList();
	}

	@Override
	public void markAsRead(Long notificationId) {
		User user = getAuthenticatedUser();
		Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
				.orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
		if (!notification.getIsRead()) {
			notification.setIsRead(true);
			notificationRepository.save(notification);
		}
	}

	@Override
	public void markAllAsRead() {
		User user = getAuthenticatedUser();
		List<Notification> notifications = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
		notifications.forEach(notification -> notification.setIsRead(true));
		notificationRepository.saveAll(notifications);
	}

	@Override
	public long getUnreadNotificationCount() {
		User user = getAuthenticatedUser();
		return notificationRepository.countByUserAndIsReadFalse(user);
	}

	private User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private NotificationResponse mapToResponse(Notification notification) {
		return NotificationResponse.builder().id(notification.getId()).title(notification.getTitle())
				.message(notification.getMessage()).isRead(notification.getIsRead())
				.notificationType(notification.getNotificationType()).createdAt(notification.getCreatedAt()).build();
	}

	private void sendNotificationEmail(String email, String title, String message) {
		try {
			emailService.sendEmail(email, title, message);
		} catch (Exception ex) {
			// Notification should not fail
			// because email failed
			// ex.printStackTrace();
			log.error("Failed to send notification email to {}", email, ex);
		}
	}
}