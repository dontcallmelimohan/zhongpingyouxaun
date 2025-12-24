package com.zpyx.zhongpingyouxuan.controller;

import com.zpyx.zhongpingyouxuan.dto.request.ProductCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.ProductUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.ProductDetailResponse;
import com.zpyx.zhongpingyouxuan.service.ProductService;
import com.zpyx.zhongpingyouxuan.util.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDetailResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Sort sorting = Sort.by("createdAt").descending();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1];
                
                if ("createdAt".equals(field) || "reviewCount".equals(field) || "averageRating".equals(field)) {
                    sorting = "desc".equalsIgnoreCase(direction) ? 
                             Sort.by(field).descending() : Sort.by(field).ascending();
                }
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sorting);
        return ResponseEntity.ok(productService.findAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDetailResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Sort sorting = Sort.by("createdAt").descending();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1];
                
                if ("createdAt".equals(field) || "reviewCount".equals(field) || "averageRating".equals(field)) {
                    sorting = "desc".equalsIgnoreCase(direction) ? 
                             Sort.by(field).descending() : Sort.by(field).ascending();
                }
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sorting);
        return ResponseEntity.ok(productService.findProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDetailResponse>> searchProductsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Sort sorting = Sort.by("createdAt").descending();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1];
                
                if ("createdAt".equals(field) || "reviewCount".equals(field) || "averageRating".equals(field)) {
                    sorting = "desc".equalsIgnoreCase(direction) ? 
                             Sort.by(field).descending() : Sort.by(field).ascending();
                }
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sorting);
        return ResponseEntity.ok(productService.searchProductsByName(name, pageable));
    }

    // 根据地区查询商品的API端点
    @GetMapping("/province/{province}")
    public ResponseEntity<Page<ProductDetailResponse>> getProductsByProvince(
            @PathVariable String province,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Sort sorting = Sort.by("createdAt").descending();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1];
                
                if ("createdAt".equals(field) || "reviewCount".equals(field) || "averageRating".equals(field)) {
                    sorting = "desc".equalsIgnoreCase(direction) ? 
                             Sort.by(field).descending() : Sort.by(field).ascending();
                }
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sorting);
        return ResponseEntity.ok(productService.findProductsByProvince(province, pageable));
    }

    @GetMapping("/province/{province}/city/{city}")
    public ResponseEntity<Page<ProductDetailResponse>> getProductsByProvinceAndCity(
            @PathVariable String province,
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Sort sorting = Sort.by("createdAt").descending();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1];
                
                if ("createdAt".equals(field) || "reviewCount".equals(field) || "averageRating".equals(field)) {
                    sorting = "desc".equalsIgnoreCase(direction) ? 
                             Sort.by(field).descending() : Sort.by(field).ascending();
                }
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sorting);
        return ResponseEntity.ok(productService.findProductsByProvinceAndCity(province, city, pageable));
    }

    @GetMapping("/province/{province}/city/{city}/area/{area}")
    public ResponseEntity<Page<ProductDetailResponse>> getProductsByProvinceAndCityAndArea(
            @PathVariable String province,
            @PathVariable String city,
            @PathVariable String area,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Sort sorting = Sort.by("createdAt").descending();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1];
                
                if ("createdAt".equals(field) || "reviewCount".equals(field) || "averageRating".equals(field)) {
                    sorting = "desc".equalsIgnoreCase(direction) ? 
                             Sort.by(field).descending() : Sort.by(field).ascending();
                }
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sorting);
        return ResponseEntity.ok(productService.findProductsByProvinceAndCityAndArea(province, city, area, pageable));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ProductDetailResponse> createProduct(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("merchantId") Long merchantId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Principal principal) throws IOException {
        
        ProductCreateRequest productRequest = new ProductCreateRequest();
        productRequest.setName(name);
        productRequest.setDescription(description);
        productRequest.setCategoryId(categoryId);
        productRequest.setMerchantId(merchantId);
        
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(imageFile, FileUploadUtil.PRODUCTS_IMG_DIR);
            productRequest.setImageUrls("/static/productsImg/" + fileName);
        }
        
        return ResponseEntity.ok(productService.createProduct(productRequest, principal.getName()));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ProductDetailResponse> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("merchantId") Long merchantId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "existingImageUrl", required = false) String existingImageUrl,
            Principal principal) throws IOException {
        
        ProductUpdateRequest productRequest = new ProductUpdateRequest();
        productRequest.setName(name);
        productRequest.setDescription(description);
        productRequest.setCategoryId(categoryId);
        productRequest.setMerchantId(merchantId);
        
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(imageFile, FileUploadUtil.PRODUCTS_IMG_DIR);
            productRequest.setImageUrls("/static/productsImg/" + fileName);
        } else if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            // 如果没有上传新图片但提供了existingImageUrl，则使用现有图片URL
            productRequest.setImageUrls(existingImageUrl);
        }
        
        return ResponseEntity.ok(productService.updateProduct(id, productRequest, principal.getName()));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT')")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id,
            Principal principal) {
        productService.deleteProduct(id, principal.getName());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/my-products")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT')")
    public ResponseEntity<Page<ProductDetailResponse>> getMerchantProducts(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.findMerchantProducts(principal.getName(), pageable));
    }
    
    
    @PostMapping("/{productId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> likeProduct(@PathVariable Long productId, Principal principal) {
        return productService.likeProduct(productId, principal.getName());
    }
    
    @DeleteMapping("/{productId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> unlikeProduct(@PathVariable Long productId, Principal principal) {
        return productService.unlikeProduct(productId, principal.getName());
    }
    
    @GetMapping("/{productId}/like/status")
    public ResponseEntity<Boolean> checkIfProductIsLiked(@PathVariable Long productId, Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(productService.checkIfProductIsLiked(productId, principal.getName()));
    }
    
    
    @PostMapping("/{productId}/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> favoriteProduct(@PathVariable Long productId, Principal principal) {
        return productService.favoriteProduct(productId, principal.getName());
    }
    
    @DeleteMapping("/{productId}/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> unfavoriteProduct(@PathVariable Long productId, Principal principal) {
        return productService.unfavoriteProduct(productId, principal.getName());
    }
    
    @GetMapping("/{productId}/favorite/status")
    public ResponseEntity<Boolean> checkIfProductIsFavorited(@PathVariable Long productId, Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(productService.checkIfProductIsFavorited(productId, principal.getName()));
    }
}