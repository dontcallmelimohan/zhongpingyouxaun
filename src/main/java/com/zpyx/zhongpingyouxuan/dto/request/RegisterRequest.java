package com.zpyx.zhongpingyouxuan.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50之间")
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100, message = "密码长度必须在6到100之间")
    private String password;
}