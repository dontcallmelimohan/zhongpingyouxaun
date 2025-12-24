package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.dto.request.UserProfileUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.UserUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.UserProfileResponse;
import com.zpyx.zhongpingyouxuan.entity.Review;
import com.zpyx.zhongpingyouxuan.entity.Role;
import com.zpyx.zhongpingyouxuan.entity.Role.ERole;
import com.zpyx.zhongpingyouxuan.entity.User;
import com.zpyx.zhongpingyouxuan.exception.ResourceNotFoundException;
import com.zpyx.zhongpingyouxuan.repository.ReviewRepository;
import com.zpyx.zhongpingyouxuan.repository.RoleRepository;
import com.zpyx.zhongpingyouxuan.repository.UserRepository;
import com.zpyx.zhongpingyouxuan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse findUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());

        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), roles);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        
        user.setUsername(userUpdateRequest.getUsername());
        user.setEmail(userUpdateRequest.getEmail());
        
        
        if (userUpdateRequest.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            
            try {
                
                if (userUpdateRequest.getRoles() instanceof String[]) {
                    for (String roleName : (String[]) userUpdateRequest.getRoles()) {
                        processRoleName(roleName, roles);
                    }
                }
                
                else if (userUpdateRequest.getRoles() instanceof Iterable) {
                    for (Object roleObj : (Iterable<?>) userUpdateRequest.getRoles()) {
                        processRoleName(roleObj.toString(), roles);
                    }
                }
                
                else {
                    processRoleName(userUpdateRequest.getRoles().toString(), roles);
                }
                
                
                if (!roles.isEmpty()) {
                    user.setRoles(roles);
                }
            } catch (Exception e) {
                throw new RuntimeException("角色处理失败: " + e.getMessage());
            }
        }
        
        return userRepository.save(user);
    }
    
    
    private void processRoleName(String roleName, Set<Role> roles) {
        if (roleName != null && !roleName.trim().isEmpty()) {
            try {
                
                roleName = roleName.trim();
                if (roleName.startsWith("\"") || roleName.startsWith("'")) {
                    roleName = roleName.substring(1);
                }
                if (roleName.endsWith("\"") || roleName.endsWith("'")) {
                    roleName = roleName.substring(0, roleName.length() - 1);
                }
                roleName = roleName.trim();
                
                ERole eRole = ERole.valueOf(roleName);
                Role role = roleRepository.findByName(eRole)
                        .orElseThrow(() -> new RuntimeException("角色未找到: " + eRole));
                roles.add(role);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的角色: " + roleName);
            }
        }
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    public Page<User> searchUsersByUsername(String username, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        
        
        
        List<Review> userReviews = reviewRepository.findByUserId(userId, Pageable.unpaged()).toList();
        reviewRepository.deleteAll(userReviews);
        
        
        userRepository.delete(user);
    }
    
    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(String username, UserProfileUpdateRequest userProfileUpdateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        
        if (userProfileUpdateRequest.getUsername() != null && !userProfileUpdateRequest.getUsername().trim().isEmpty()) {
            user.setUsername(userProfileUpdateRequest.getUsername().trim());
        }
        
        
        if (userProfileUpdateRequest.getEmail() != null && !userProfileUpdateRequest.getEmail().trim().isEmpty()) {
            user.setEmail(userProfileUpdateRequest.getEmail().trim());
        }
        
        
        if (userProfileUpdateRequest.getPassword() != null && !userProfileUpdateRequest.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userProfileUpdateRequest.getPassword().trim()));
        }
        
        
        User updatedUser = userRepository.save(user);
        
        
        List<String> roles = updatedUser.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());
        
        return new UserProfileResponse(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(), roles);
    }
}