package com.kusuma.payment_engine.entity;

import com.kusuma.payment_engine.enums.NotificationType;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "notifications", indexes = { @Index(name = "idx_notification_user", columnList = "user_id"),
		@Index(name = "idx_notification_read", columnList = "isRead") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 500)
	private String message;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isRead = false;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;
}