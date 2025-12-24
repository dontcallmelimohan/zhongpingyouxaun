package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.dto.request.ProductCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.ProductUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.ProductDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminProductService {
    ProductDetailResponse createProduct(ProductCreateRequest productRequest);
    ProductDetailResponse updateProduct(Long productId, ProductUpdateRequest productRequest);
    void deleteProduct(Long productId);
}