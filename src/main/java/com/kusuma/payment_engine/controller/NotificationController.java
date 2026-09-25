package com.kusuma.payment_engine.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusuma.payment_engine.dto.response.NotificationResponse;
import com.kusuma.payment_engine.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
		return ResponseEntity.ok(notificationService.getMyNotifications());
	}

	@GetMapping("/unread")
	public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
		return ResponseEntity.ok(notificationService.getUnreadNotifications());
	}

	@PutMapping("/{notificationId}/read")
	public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
		notificationService.markAsRead(notificationId);
		return ResponseEntity.ok("Notification marked as read");
	}

	@PutMapping("/read-all")
	public ResponseEntity<String> markAllAsRead() {
		notificationService.markAllAsRead();
		return ResponseEntity.ok("All notifications marked as read");
	}
}