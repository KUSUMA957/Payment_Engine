package com.kusuma.payment_engine.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.Beneficiary;
import com.kusuma.payment_engine.entity.User;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

	List<Beneficiary> findByOwnerUserOrderByCreatedAtDesc(User ownerUser);

	Optional<Beneficiary> findByIdAndOwnerUser(Long id, User ownerUser);

	boolean existsByOwnerUserAndBeneficiaryAccount(User ownerUser, Account beneficiaryAccount);

	List<Beneficiary> findByOwnerUser(User ownerUser);
}