package com.zpyx.zhongpingyouxuan.dto.request;

import lombok.Data;

@Data
public class CarouselUpdateRequest {
    private String imageUrl;
    private String targetUrl;
    private Integer displayOrder;
}