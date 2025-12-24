package com.zpyx.zhongpingyouxuan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductUpdateRequest {
    
    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String description;
    
    private String imageUrls;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "商家ID不能为空")
    private Long merchantId;
}