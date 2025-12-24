package com.zpyx.zhongpingyouxuan.controller.admin;

import com.zpyx.zhongpingyouxuan.dto.request.CarouselUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MessageResponse;
import com.zpyx.zhongpingyouxuan.entity.Carousel;
import com.zpyx.zhongpingyouxuan.service.CarouselService;
import com.zpyx.zhongpingyouxuan.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/carousels")
public class AdminCarouselController {

    @Autowired
    private CarouselService carouselService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Carousel>> getAllCarousels() {
        return ResponseEntity.ok(carouselService.getAllCarousels());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Carousel> createCarousel(
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam("targetUrl") String targetUrl,
            @RequestParam("displayOrder") Integer displayOrder) throws IOException {
        
        Carousel carousel = new Carousel();
        carousel.setTargetUrl(targetUrl);
        carousel.setDisplayOrder(displayOrder);
        
        
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(imageFile, FileUploadUtil.BANNER_IMG_DIR);
            carousel.setImageUrl("/static/bannerimg/" + fileName);
        }
        
        return ResponseEntity.ok(carouselService.createCarousel(carousel));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Carousel> updateCarousel(
            @PathVariable Long id,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "targetUrl", required = false) String targetUrl,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder) throws IOException {
        
        CarouselUpdateRequest carouselUpdateRequest = new CarouselUpdateRequest();
        carouselUpdateRequest.setTargetUrl(targetUrl);
        carouselUpdateRequest.setDisplayOrder(displayOrder);
        
        
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(imageFile, FileUploadUtil.BANNER_IMG_DIR);
            carouselUpdateRequest.setImageUrl("/static/bannerimg/" + fileName);
        }
        
        return ResponseEntity.ok(carouselService.updateCarousel(id, carouselUpdateRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCarousel(@PathVariable Long id) {
        carouselService.deleteCarousel(id);
        return ResponseEntity.ok(new MessageResponse("轮播图删除成功"));
    }
}