package com.kusuma.payment_engine.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String message;
}
