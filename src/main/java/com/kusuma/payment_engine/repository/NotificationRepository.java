package com.kusuma.payment_engine.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusuma.payment_engine.entity.Notification;
import com.kusuma.payment_engine.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserOrderByCreatedAtDesc(User user);

	List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

	Optional<Notification> findByIdAndUser(Long notificationId, User user);
}