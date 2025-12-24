package com.zpyx.zhongpingyouxuan.controller.admin;

import com.zpyx.zhongpingyouxuan.dto.request.ReviewUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MessageResponse;
import com.zpyx.zhongpingyouxuan.dto.response.ReviewResponse;
import com.zpyx.zhongpingyouxuan.entity.Review;
import com.zpyx.zhongpingyouxuan.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ReviewResponse> getAllReviews(Pageable pageable, @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return reviewService.searchReviews(search, pageable);
        }
        
        return reviewService.findAllReviewResponses(pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createReview() {
        return ResponseEntity.badRequest().body(new MessageResponse("管理员不能直接创建评论"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        Review updatedReview = reviewService.updateReview(id, reviewUpdateRequest);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(new MessageResponse("评论删除成功"));
    }
}