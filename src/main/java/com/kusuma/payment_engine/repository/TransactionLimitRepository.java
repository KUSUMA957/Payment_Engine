package com.kusuma.payment_engine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusuma.payment_engine.entity.TransactionLimit;
import com.kusuma.payment_engine.enums.TransactionLimitType;

public interface TransactionLimitRepository extends JpaRepository<TransactionLimit, Long> {

	Optional<TransactionLimit> findByLimitTypeAndActiveTrue(TransactionLimitType limitType);

	Optional<TransactionLimit> findByLimitType(TransactionLimitType limitType);

}