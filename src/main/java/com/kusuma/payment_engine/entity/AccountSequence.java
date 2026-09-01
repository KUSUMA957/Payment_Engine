package com.kusuma.payment_engine.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_sequence")
@Getter
@Setter
@NoArgsConstructor
public class AccountSequence {

    @Id
    private Long id;

    private Long nextValue;
}