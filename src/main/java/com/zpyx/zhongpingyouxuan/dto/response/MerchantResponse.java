package com.zpyx.zhongpingyouxuan.dto.response;

import lombok.Data;

@Data
public class MerchantResponse {
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private String address;
    
    // 地区相关字段
    private String province;
    
    private String city;
    
    private String area;
    
    private String ownerUsername;
}