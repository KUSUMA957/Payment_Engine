package com.kusuma.payment_engine.dto.response;

import java.time.LocalDateTime;

import com.kusuma.payment_engine.enums.NotificationType;

import lombok.Builder;

@Builder
public record NotificationResponse(

		Long id,

		String title,

		String message,

		Boolean isRead,

		NotificationType notificationType,

		LocalDateTime createdAt) {
}