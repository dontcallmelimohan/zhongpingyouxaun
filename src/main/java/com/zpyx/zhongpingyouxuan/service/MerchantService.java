package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.dto.request.MerchantCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.MerchantUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MerchantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MerchantService {
    
    MerchantResponse createMerchant(MerchantCreateRequest request, String username);
    
    Page<MerchantResponse> getAllMerchants(Pageable pageable);
    
    MerchantResponse getMerchantById(Long id);
    
    MerchantResponse getCurrentUserMerchant(String username);
    
    MerchantResponse updateMerchant(Long id, MerchantUpdateRequest request, String username);
    
    void deleteMerchant(Long id, String username);
}