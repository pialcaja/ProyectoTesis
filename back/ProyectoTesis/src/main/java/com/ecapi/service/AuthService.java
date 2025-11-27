package com.ecapi.service;

import com.ecapi.dto.AuthResponse;
import com.ecapi.dto.LoginRequest;
import com.ecapi.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(String refreshToken);
}

