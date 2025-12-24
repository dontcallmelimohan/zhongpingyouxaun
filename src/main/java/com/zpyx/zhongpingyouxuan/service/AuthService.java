package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.dto.request.LoginRequest;
import com.zpyx.zhongpingyouxuan.dto.request.RegisterRequest;
import com.zpyx.zhongpingyouxuan.dto.response.JwtResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> registerUser(RegisterRequest registerRequest);
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
}