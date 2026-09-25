package com.kusuma.payment_engine.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "beneficiaries", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "owner_user_id", "beneficiary_account_id" }) })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_user_id", nullable = false)
	private User ownerUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beneficiary_account_id", nullable = false)
	private Account beneficiaryAccount;

	@Column(nullable = false)
	private String nickname;
}
