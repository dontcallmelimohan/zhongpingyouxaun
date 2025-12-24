package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.dto.request.ProductCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.ProductUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.ProductDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    Page<ProductDetailResponse> findAllProducts(Pageable pageable);
    Page<ProductDetailResponse> findProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductDetailResponse> searchProductsByName(String name, Pageable pageable);
    ProductDetailResponse findProductById(Long productId);

    Page<ProductDetailResponse> findProductsByProvince(String province, Pageable pageable);
    Page<ProductDetailResponse> findProductsByProvinceAndCity(String province, String city, Pageable pageable);
    Page<ProductDetailResponse> findProductsByProvinceAndCityAndArea(String province, String city, String area, Pageable pageable);

    ProductDetailResponse createProduct(ProductCreateRequest productRequest, String username);

    ProductDetailResponse updateProduct(Long productId, ProductUpdateRequest productRequest, String username);

    void deleteProduct(Long productId, String username);
    
    Page<ProductDetailResponse> findMerchantProducts(String username, Pageable pageable);
    
    void updateProductRating(Long productId);
    
    
    ResponseEntity<?> likeProduct(Long productId, String username);
    ResponseEntity<?> unlikeProduct(Long productId, String username);
    boolean checkIfProductIsLiked(Long productId, String username);
    
    
    ResponseEntity<?> favoriteProduct(Long productId, String username);
    ResponseEntity<?> unfavoriteProduct(Long productId, String username);
    boolean checkIfProductIsFavorited(Long productId, String username);
}