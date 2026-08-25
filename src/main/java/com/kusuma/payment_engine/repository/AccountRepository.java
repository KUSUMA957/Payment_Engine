package com.kusuma.payment_engine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusuma.payment_engine.entity.Account;
import com.kusuma.payment_engine.entity.User;

public interface AccountRepository extends JpaRepository<Account, Long> {

	boolean existsByUser(User user);

	Optional<Account> findByUser(User user);

	Optional<Account> findByAccountNumber(String accountNumber);
}