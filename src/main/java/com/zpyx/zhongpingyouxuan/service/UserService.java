package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.dto.request.UserProfileUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.UserUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.UserProfileResponse;
import com.zpyx.zhongpingyouxuan.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    UserProfileResponse findUserProfileByUsername(String username);
    User updateUser(Long userId, UserUpdateRequest userUpdateRequest);
    UserProfileResponse updateUserProfile(String username, UserProfileUpdateRequest userProfileUpdateRequest);
    Optional<User> findById(Long userId);
    User saveUser(User user);
    Page<User> findAll(Pageable pageable);
    Page<User> searchUsersByUsername(String username, Pageable pageable);
    void deleteUser(Long userId);
}