package com.zpyx.zhongpingyouxuan.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrls;
    private Double averageRating;
    private Integer reviewCount;
    private Integer likesCount;
    private Integer favoritesCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Long categoryId; // 添加categoryId字段，用于前端获取商品分类ID
    private String categoryName;
    private Long merchantId;
    private String merchantName;
    // 商家地区信息
    private String merchantProvince;
    private String merchantCity;
    private String merchantArea;
}