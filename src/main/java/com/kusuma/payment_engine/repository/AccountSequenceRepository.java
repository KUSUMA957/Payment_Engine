package com.kusuma.payment_engine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kusuma.payment_engine.entity.AccountSequence;

import jakarta.persistence.LockModeType;

public interface AccountSequenceRepository extends JpaRepository<AccountSequence, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT a
			FROM AccountSequence a
			WHERE a.id = :id
			""")
	Optional<AccountSequence> findByIdForUpdate(@Param("id")Long id);
}
