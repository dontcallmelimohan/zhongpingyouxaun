package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.dto.request.LoginRequest;
import com.zpyx.zhongpingyouxuan.dto.request.RegisterRequest;
import com.zpyx.zhongpingyouxuan.dto.response.JwtResponse;
import com.zpyx.zhongpingyouxuan.dto.response.MessageResponse;
import com.zpyx.zhongpingyouxuan.entity.Role;
import com.zpyx.zhongpingyouxuan.entity.User;
import com.zpyx.zhongpingyouxuan.repository.RoleRepository;
import com.zpyx.zhongpingyouxuan.repository.UserRepository;
import com.zpyx.zhongpingyouxuan.security.jwt.JwtUtils;
import com.zpyx.zhongpingyouxuan.security.services.UserDetailsImpl;
import com.zpyx.zhongpingyouxuan.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        try {
            logger.debug("Attempting to authenticate user: {}", loginRequest.getUsername());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user != null) {
                logger.debug("Stored password hash: {}", user.getPassword());
                logger.debug("Password encoder match result: {}", encoder.matches(loginRequest.getPassword(), user.getPassword()));
            }
            throw e;
        }
    }

    @Override
    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("错误: 用户名已存在!"));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("错误: 邮箱已被注册!"));
        }

        User user = new User(registerRequest.getUsername(),
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("错误: 角色未找到."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("用户注册成功!"));
    }
}