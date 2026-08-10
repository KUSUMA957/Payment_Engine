package com.kusuma.payment_engine.service;

import com.kusuma.payment_engine.dto.request.RegisterRequest;
import com.kusuma.payment_engine.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);
}
