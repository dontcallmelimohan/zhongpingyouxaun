package com.zpyx.zhongpingyouxuan.controller;

import com.zpyx.zhongpingyouxuan.dto.request.MerchantCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.MerchantUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MerchantResponse;
import com.zpyx.zhongpingyouxuan.service.MerchantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    
    @Autowired
    private MerchantService merchantService;
    
    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<MerchantResponse> createMerchant(
            @Valid @RequestBody MerchantCreateRequest request, 
            Principal principal) {
        return ResponseEntity.ok(merchantService.createMerchant(request, principal.getName()));
    }
    
    @GetMapping
    public ResponseEntity<Page<MerchantResponse>> getAllMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(merchantService.getAllMerchants(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MerchantResponse> getMerchantById(@PathVariable Long id) {
        return ResponseEntity.ok(merchantService.getMerchantById(id));
    }
    
    @GetMapping("/my-merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<MerchantResponse> getCurrentUserMerchant(Principal principal) {
        return ResponseEntity.ok(merchantService.getCurrentUserMerchant(principal.getName()));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<MerchantResponse> updateMerchant(
            @PathVariable Long id,
            @Valid @RequestBody MerchantUpdateRequest request,
            Principal principal) {
        return ResponseEntity.ok(merchantService.updateMerchant(id, request, principal.getName()));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> deleteMerchant(
            @PathVariable Long id,
            Principal principal) {
        merchantService.deleteMerchant(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}