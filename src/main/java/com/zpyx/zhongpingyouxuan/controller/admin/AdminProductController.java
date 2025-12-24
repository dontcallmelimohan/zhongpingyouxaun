package com.zpyx.zhongpingyouxuan.controller.admin;

import com.zpyx.zhongpingyouxuan.dto.request.ProductCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.ProductUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MessageResponse;
import com.zpyx.zhongpingyouxuan.dto.response.ProductDetailResponse;
import com.zpyx.zhongpingyouxuan.service.AdminProductService;
import com.zpyx.zhongpingyouxuan.service.ProductService;
import com.zpyx.zhongpingyouxuan.util.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;


@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')") 
public class AdminProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private AdminProductService adminProductService;

    @PostMapping
    public ResponseEntity<ProductDetailResponse> createProduct(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("merchantId") Long merchantId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        
        ProductCreateRequest productRequest = new ProductCreateRequest();
        productRequest.setName(name);
        productRequest.setDescription(description);
        productRequest.setCategoryId(categoryId);
        productRequest.setMerchantId(merchantId);
        
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(imageFile, FileUploadUtil.PRODUCTS_IMG_DIR);
            productRequest.setImageUrls("/static/productsImg/" + fileName);
        }
        
        ProductDetailResponse newProduct = adminProductService.createProduct(productRequest);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<Page<ProductDetailResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(productService.searchProductsByName(search, pageable));
        }
        return ResponseEntity.ok(productService.findAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("merchantId") Long merchantId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "existingImageUrl", required = false) String existingImageUrl) throws IOException {
        
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
        
        ProductDetailResponse updatedProduct = adminProductService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.ok(new MessageResponse("Product deleted successfully!"));
    }
}