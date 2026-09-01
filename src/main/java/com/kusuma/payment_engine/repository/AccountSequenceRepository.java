package com.kusuma.payment_engine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import com.kusuma.payment_engine.entity.AccountSequence;

public interface AccountSequenceRepository extends JpaRepository<AccountSequence, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT a
			FROM AccountSequence a
			WHERE a.id = :id
			""")
	Optional<AccountSequence> findByIdForUpdate(Long id);
}
