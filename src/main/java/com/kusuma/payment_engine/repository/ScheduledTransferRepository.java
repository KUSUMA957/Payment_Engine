package com.kusuma.payment_engine.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusuma.payment_engine.entity.Beneficiary;
import com.kusuma.payment_engine.entity.ScheduledTransfer;
import com.kusuma.payment_engine.entity.User;
import com.kusuma.payment_engine.enums.ScheduledTransferStatus;

public interface ScheduledTransferRepository extends JpaRepository<ScheduledTransfer, Long> {

	List<ScheduledTransfer> findByUserOrderByScheduledAtDesc(User user);

	Optional<ScheduledTransfer> findByIdAndUser(Long id, User user);

	List<ScheduledTransfer> findByStatusAndNextExecutionDateLessThanEqual(ScheduledTransferStatus status,
			LocalDate nextExecutionDate);

	List<ScheduledTransfer> findByUserAndStatusOrderByNextExecutionDateAsc(User user, ScheduledTransferStatus status);

	boolean existsByBeneficiaryAndStatusIn(Beneficiary beneficiary, List<ScheduledTransferStatus> statuses);
}