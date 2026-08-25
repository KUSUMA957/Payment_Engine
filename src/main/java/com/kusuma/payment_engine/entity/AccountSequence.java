package com.kusuma.payment_engine.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AccountSequence {

	@Id
	private Long id;

	private Long nextValue;
}