package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.dto.request.MerchantCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.MerchantUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MerchantResponse;
import com.zpyx.zhongpingyouxuan.entity.Merchant;
import com.zpyx.zhongpingyouxuan.entity.User;
import com.zpyx.zhongpingyouxuan.repository.MerchantRepository;
import com.zpyx.zhongpingyouxuan.repository.UserRepository;
import com.zpyx.zhongpingyouxuan.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Service
public class MerchantServiceImpl implements MerchantService {
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public MerchantResponse createMerchant(MerchantCreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
        
        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setDescription(request.getDescription());
        merchant.setAddress(request.getAddress());
        merchant.setProvince(request.getProvince());
        merchant.setCity(request.getCity());
        merchant.setArea(request.getArea());
        merchant.setOwner(user);
        
        Merchant savedMerchant = merchantRepository.save(merchant);
        return convertToResponse(savedMerchant);
    }
    
    @Override
    public Page<MerchantResponse> getAllMerchants(Pageable pageable) {
        return merchantRepository.findAll(pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public MerchantResponse getMerchantById(Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Merchant not found with id: " + id));
        return convertToResponse(merchant);
    }
    
    @Override
    public MerchantResponse getCurrentUserMerchant(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
        
        Merchant merchant = merchantRepository.findByOwner_Id(user.getId())
                .orElseThrow(() -> new NoSuchElementException("No merchant found for this user"));
        
        return convertToResponse(merchant);
    }
    
    @Override
    @Transactional
    public MerchantResponse updateMerchant(Long id, MerchantUpdateRequest request, String username) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Merchant not found with id: " + id));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
        
        
        if (!merchant.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to update this merchant");
        }
        
        merchant.setName(request.getName());
        merchant.setDescription(request.getDescription());
        merchant.setAddress(request.getAddress());
        merchant.setProvince(request.getProvince());
        merchant.setCity(request.getCity());
        merchant.setArea(request.getArea());
        
        Merchant updatedMerchant = merchantRepository.save(merchant);
        return convertToResponse(updatedMerchant);
    }
    
    @Override
    @Transactional
    public void deleteMerchant(Long id, String username) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Merchant not found with id: " + id));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
        
        
        if (!merchant.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to delete this merchant");
        }
        
        merchantRepository.delete(merchant);
    }
    
    private MerchantResponse convertToResponse(Merchant merchant) {
        MerchantResponse response = new MerchantResponse();
        response.setId(merchant.getId());
        response.setName(merchant.getName());
        response.setDescription(merchant.getDescription());
        response.setAddress(merchant.getAddress());
        response.setProvince(merchant.getProvince());
        response.setCity(merchant.getCity());
        response.setArea(merchant.getArea());
        response.setOwnerUsername(merchant.getOwner().getUsername());
        return response;
    }
}