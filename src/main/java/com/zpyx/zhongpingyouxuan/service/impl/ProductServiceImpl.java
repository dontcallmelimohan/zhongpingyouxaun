package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.dto.request.ProductCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.ProductUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.ProductDetailResponse;
import com.zpyx.zhongpingyouxuan.entity.*;
import com.zpyx.zhongpingyouxuan.exception.ResourceNotFoundException;
import com.zpyx.zhongpingyouxuan.exception.UnauthorizedException;
import com.zpyx.zhongpingyouxuan.repository.*;
import com.zpyx.zhongpingyouxuan.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductLikeRepository productLikeRepository;
    
    @Autowired
    private ProductFavoriteRepository productFavoriteRepository;

    @Override
    public Page<ProductDetailResponse> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public Page<ProductDetailResponse> findProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ProductDetailResponse> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable).map(this::convertToDto);
    }

    @Override
    public ProductDetailResponse findProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return convertToDto(product);
    }

    @Override
    public Page<ProductDetailResponse> findProductsByProvince(String province, Pageable pageable) {
        return productRepository.findByMerchantProvince(province, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ProductDetailResponse> findProductsByProvinceAndCity(String province, String city, Pageable pageable) {
        return productRepository.findByMerchantProvinceAndMerchantCity(province, city, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ProductDetailResponse> findProductsByProvinceAndCityAndArea(String province, String city, String area, Pageable pageable) {
        return productRepository.findByMerchantProvinceAndMerchantCityAndMerchantArea(province, city, area, pageable).map(this::convertToDto);
    }

    private ProductDetailResponse convertToDto(Product product) {
        ProductDetailResponse dto = new ProductDetailResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImageUrls(product.getImageUrls());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setLikesCount(product.getLikesCount());
        dto.setFavoritesCount(product.getFavoritesCount());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setCategoryId(product.getCategory().getId()); // 设置categoryId
        dto.setCategoryName(product.getCategory().getName());
        dto.setMerchantId(product.getMerchant().getId());
        dto.setMerchantName(product.getMerchant().getName());
        // 设置商家地区信息
        dto.setMerchantProvince(product.getMerchant().getProvince());
        dto.setMerchantCity(product.getMerchant().getCity());
        dto.setMerchantArea(product.getMerchant().getArea());
        return dto;
    }
    
    @Override
    @Transactional
    public ProductDetailResponse createProduct(ProductCreateRequest productRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        
        Merchant merchant = merchantRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a merchant"));
        
        
        if (!merchant.getId().equals(productRequest.getMerchantId())) {
            throw new UnauthorizedException("You can only create products for your own merchant");
        }
        
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
    public ProductDetailResponse updateProduct(Long productId, ProductUpdateRequest productRequest, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        
        if (!product.getMerchant().getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this product");
        }

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setImageUrls(productRequest.getImageUrls());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        
        if (!product.getMerchant().getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this product");
        }
        
        productRepository.delete(product);
    }
    
    @Override
    public Page<ProductDetailResponse> findMerchantProducts(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Merchant merchant = merchantRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a merchant"));
        
        return productRepository.findByMerchantId(merchant.getId(), pageable)
                .map(this::convertToDto);
    }
    
    @Override
    @Transactional
    public void updateProductRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        Double averageRating = reviewRepository.calculateAverageRatingByProductId(productId);
        Integer reviewCount = reviewRepository.countByProductId(productId);
        
        product.setAverageRating(averageRating != null ? averageRating : 0.0);
        product.setReviewCount(reviewCount);
        
        productRepository.save(product);
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> likeProduct(Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        if (productLikeRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            return ResponseEntity.badRequest().body("您已经点赞过该商品");
        }
        
        
        ProductLike like = new ProductLike(user, product);
        productLikeRepository.save(like);
        
        
        Product refreshedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        long actualLikesCount = productLikeRepository.countByProductId(productId);
        refreshedProduct.setLikesCount((int) actualLikesCount);
        productRepository.save(refreshedProduct);
        
        return ResponseEntity.ok().build();
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> unlikeProduct(Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        if (!productLikeRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            return ResponseEntity.badRequest().body("您还没有点赞该商品");
        }
        
        
        productLikeRepository.deleteByUserIdAndProductId(user.getId(), productId);
        
        
        Product refreshedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        long actualLikesCount = productLikeRepository.countByProductId(productId);
        refreshedProduct.setLikesCount((int) actualLikesCount);
        productRepository.save(refreshedProduct);
        
        return ResponseEntity.ok().build();
    }
    
    @Override
    public boolean checkIfProductIsLiked(Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return productLikeRepository.existsByUserIdAndProductId(user.getId(), productId);
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> favoriteProduct(Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        if (productFavoriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            return ResponseEntity.badRequest().body("您已经收藏过该商品");
        }
        
        
        ProductFavorite favorite = new ProductFavorite(user, product);
        productFavoriteRepository.save(favorite);
        
        
        Product refreshedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        long actualFavoritesCount = productFavoriteRepository.countByProductId(productId);
        refreshedProduct.setFavoritesCount((int) actualFavoritesCount);
        productRepository.save(refreshedProduct);
        
        return ResponseEntity.ok().build();
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> unfavoriteProduct(Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        if (!productFavoriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            return ResponseEntity.badRequest().body("您还没有收藏该商品");
        }
        
        
        productFavoriteRepository.deleteByUserIdAndProductId(user.getId(), productId);
        
        
        Product refreshedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        
        long actualFavoritesCount = productFavoriteRepository.countByProductId(productId);
        refreshedProduct.setFavoritesCount((int) actualFavoritesCount);
        productRepository.save(refreshedProduct);
        
        return ResponseEntity.ok().build();
    }
    
    @Override
    public boolean checkIfProductIsFavorited(Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return productFavoriteRepository.existsByUserIdAndProductId(user.getId(), productId);
    }
}

