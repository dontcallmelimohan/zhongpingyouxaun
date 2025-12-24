package com.zpyx.zhongpingyouxuan.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MerchantUpdateRequest {
    
    @NotEmpty(message = "Merchant name cannot be empty")
    private String name;
    
    private String description;
    
    private String address;

    private String province;
    
    private String city;
    
    private String area;
}