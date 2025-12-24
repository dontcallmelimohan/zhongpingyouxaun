package com.zpyx.zhongpingyouxuan.dto.request;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String username;
    private String email;
    private String password; 
}