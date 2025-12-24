package com.zpyx.zhongpingyouxuan.controller;

import com.zpyx.zhongpingyouxuan.dto.request.LoginRequest;
import com.zpyx.zhongpingyouxuan.dto.request.MerchantUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.UserProfileUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MerchantLoginResponse;
import com.zpyx.zhongpingyouxuan.dto.response.MerchantResponse;
import com.zpyx.zhongpingyouxuan.dto.response.UserProfileResponse;
import com.zpyx.zhongpingyouxuan.entity.Merchant;
import com.zpyx.zhongpingyouxuan.entity.User;
import com.zpyx.zhongpingyouxuan.repository.MerchantRepository;
import com.zpyx.zhongpingyouxuan.repository.UserRepository;
import com.zpyx.zhongpingyouxuan.security.jwt.JwtUtils;
import com.zpyx.zhongpingyouxuan.service.MerchantService;
import com.zpyx.zhongpingyouxuan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/merchant")
public class MerchantAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private MerchantService merchantService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateMerchant(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            
            boolean isMerchant = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_MERCHANT"));

            if (!isMerchant) {
                return ResponseEntity.badRequest().body("错误: 该账号不是商家账号");
            }

            
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            
            Optional<Merchant> merchantOptional = merchantRepository.findByOwnerId(user.getId());
            if (merchantOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("错误: 未找到对应的商家信息");
            }

            Merchant merchant = merchantOptional.get();

            
            String jwt = jwtUtils.generateJwtToken(authentication);

            
            MerchantLoginResponse response = new MerchantLoginResponse();
            response.setToken(jwt);
            response.setMerchant(convertToMerchantDto(merchant));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("登录失败: " + e.getMessage());
        }
    }

    private MerchantLoginResponse.MerchantDto convertToMerchantDto(Merchant merchant) {
        MerchantLoginResponse.MerchantDto dto = new MerchantLoginResponse.MerchantDto();
        dto.setId(merchant.getId());
        dto.setName(merchant.getName());
        dto.setDescription(merchant.getDescription());
        dto.setAddress(merchant.getAddress());
        dto.setOwnerUsername(merchant.getOwner().getUsername());
        return dto;
    }
    
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> getMerchantProfile(Principal principal) {
        return ResponseEntity.ok(merchantService.getCurrentUserMerchant(principal.getName()));
    }
    
    
    @PutMapping("/profile")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> updateMerchantProfile(
            Principal principal,
            @RequestBody MerchantUpdateRequest merchantUpdateRequest) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("未找到用户信息"));
        
        Merchant merchant = merchantRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new RuntimeException("未找到对应的商家信息"));
        
        return ResponseEntity.ok(
                merchantService.updateMerchant(merchant.getId(), merchantUpdateRequest, principal.getName())
        );
    }
    
    
    @PutMapping("/profile/password")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> updateMerchantPassword(
            Principal principal,
            @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!passwordEncoder.matches(passwordUpdateRequest.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("当前密码不正确");
        }
        
        
        UserProfileUpdateRequest profileUpdateRequest = new UserProfileUpdateRequest();
        profileUpdateRequest.setPassword(passwordUpdateRequest.getNewPassword());
        
        return ResponseEntity.ok(
                userService.updateUserProfile(principal.getName(), profileUpdateRequest)
        );
    }
    
    
    public static class PasswordUpdateRequest {
        private String currentPassword;
        private String newPassword;
        
        public String getCurrentPassword() {
            return currentPassword;
        }
        
        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }
        
        public String getNewPassword() {
            return newPassword;
        }
        
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}