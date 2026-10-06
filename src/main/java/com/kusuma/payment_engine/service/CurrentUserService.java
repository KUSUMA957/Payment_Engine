package com.kusuma.payment_engine.service;

import com.kusuma.payment_engine.entity.User;

public interface CurrentUserService {

    User getAuthenticatedUser();

}