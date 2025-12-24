package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.dto.request.ProductCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.ProductUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.ProductDetailResponse;
import com.zpyx.zhongpingyouxuan.entity.Category;
import com.zpyx.zhongpingyouxuan.entity.Merchant;
import com.zpyx.zhongpingyouxuan.entity.Product;
import com.zpyx.zhongpingyouxuan.exception.ResourceNotFoundException;
import com.zpyx.zhongpingyouxuan.repository.CategoryRepository;
import com.zpyx.zhongpingyouxuan.repository.MerchantRepository;
import com.zpyx.zhongpingyouxuan.repository.ProductRepository;
import com.zpyx.zhongpingyouxuan.service.AdminProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    private ProductDetailResponse convertToDto(Product product) {
        ProductDetailResponse dto = new ProductDetailResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImageUrls(product.getImageUrls());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setCategoryId(product.getCategory().getId()); // 设置categoryId
        dto.setCategoryName(product.getCategory().getName());
        dto.setMerchantId(product.getMerchant().getId());
        dto.setMerchantName(product.getMerchant().getName());
        return dto;
    }

    @Override
    @Transactional
    public ProductDetailResponse createProduct(ProductCreateRequest productRequest) {
        
        Merchant merchant = merchantRepository.findById(productRequest.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", productRequest.getMerchantId()));
        
        
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setImageUrls(productRequest.getImageUrls());
        product.setCategory(category);
        product.setMerchant(merchant);

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDetailResponse updateProduct(Long productId, ProductUpdateRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        Merchant merchant = merchantRepository.findById(productRequest.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", productRequest.getMerchantId()));
        
        
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setImageUrls(productRequest.getImageUrls());
        product.setMerchant(merchant);
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        productRepository.delete(product);
    }
}