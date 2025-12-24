package com.zpyx.zhongpingyouxuan.controller.admin;

import com.zpyx.zhongpingyouxuan.dto.request.MerchantCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.MerchantUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MerchantResponse;
import com.zpyx.zhongpingyouxuan.entity.Merchant;
import com.zpyx.zhongpingyouxuan.entity.User;
import com.zpyx.zhongpingyouxuan.repository.MerchantRepository;
import com.zpyx.zhongpingyouxuan.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin/merchants")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMerchantController {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<MerchantResponse> createMerchant(@Valid @RequestBody AdminMerchantCreateRequest request) {
        
        User owner = userRepository.findByUsername(request.getOwnerUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + request.getOwnerUsername()));
        
        // 检查用户是否已拥有商家
        if (merchantRepository.existsByOwnerId(owner.getId())) {
            throw new IllegalArgumentException("该账号已绑定商家，请选择其他账号或先删除现有商家");
        }
        
        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setDescription(request.getDescription());
        merchant.setAddress(request.getAddress());
        merchant.setProvince(request.getProvince());
        merchant.setCity(request.getCity());
        merchant.setArea(request.getArea());
        merchant.setOwner(owner);
        
        
        Merchant savedMerchant = merchantRepository.save(merchant);
        
        
        MerchantResponse response = new MerchantResponse();
        response.setId(savedMerchant.getId());
        response.setName(savedMerchant.getName());
        response.setDescription(savedMerchant.getDescription());
        response.setAddress(savedMerchant.getAddress());
        response.setProvince(savedMerchant.getProvince());
        response.setCity(savedMerchant.getCity());
        response.setArea(savedMerchant.getArea());
        response.setOwnerUsername(savedMerchant.getOwner().getUsername());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MerchantResponse> updateMerchant(
            @PathVariable Long id,
            @Valid @RequestBody AdminMerchantUpdateRequest request) {
        
        
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Merchant not found with id: " + id));
        
        
        // 如果更新了所有者，检查新所有者是否已绑定商家
        if (request.getOwnerUsername() != null && !request.getOwnerUsername().isEmpty()) {
            User newOwner = userRepository.findByUsername(request.getOwnerUsername())
                    .orElseThrow(() -> new NoSuchElementException("User not found with username: " + request.getOwnerUsername()));
            
            // 检查新所有者是否已绑定商家（排除当前商家）
            if (merchantRepository.existsByOwnerIdAndIdNot(newOwner.getId(), id)) {
                throw new IllegalArgumentException("该账号已绑定商家，请选择其他账号或先删除现有商家");
            }
            
            merchant.setOwner(newOwner);
        }
        
        merchant.setName(request.getName());
        merchant.setDescription(request.getDescription());
        merchant.setAddress(request.getAddress());
        merchant.setProvince(request.getProvince());
        merchant.setCity(request.getCity());
        merchant.setArea(request.getArea());
        
        
        Merchant updatedMerchant = merchantRepository.save(merchant);
        
        
        MerchantResponse response = new MerchantResponse();
        response.setId(updatedMerchant.getId());
        response.setName(updatedMerchant.getName());
        response.setDescription(updatedMerchant.getDescription());
        response.setAddress(updatedMerchant.getAddress());
        response.setProvince(updatedMerchant.getProvince());
        response.setCity(updatedMerchant.getCity());
        response.setArea(updatedMerchant.getArea());
        response.setOwnerUsername(updatedMerchant.getOwner().getUsername());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMerchant(@PathVariable Long id) {
        if (!merchantRepository.existsById(id)) {
            throw new NoSuchElementException("Merchant not found with id: " + id);
        }
        merchantRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}


class AdminMerchantCreateRequest {
    @NotEmpty(message = "商家名称不能为空")
    private String name;
    private String description;
    private String address;
    @NotEmpty(message = "所有者用户名不能为空")
    private String ownerUsername;
    // 地区相关字段
    private String province;
    private String city;
    private String area;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}


class AdminMerchantUpdateRequest {
    @NotEmpty(message = "商家名称不能为空")
    private String name;
    private String description;
    private String address;
    private String ownerUsername;
    // 地区相关字段
    private String province;
    private String city;
    private String area;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}