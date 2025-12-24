package com.zpyx.zhongpingyouxuan.controller;

import com.zpyx.zhongpingyouxuan.dto.request.ReviewCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.ReviewResponse;
import com.zpyx.zhongpingyouxuan.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.findReviewsByProductId(productId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreateRequest reviewRequest, Principal principal) {
        return reviewService.createReview(reviewRequest, principal.getName());
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<Page<ReviewResponse>> getCurrentUserReviews(Principal principal,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.findReviewsByCurrentUser(principal.getName(), pageable));
    }

    @GetMapping("/merchant")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<Page<ReviewResponse>> getMerchantReviews(Principal principal,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(required = false) String searchTerm,
                                                                   @RequestParam(required = false) String rating,
                                                                   @RequestParam(required = false) String responseStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.findReviewsByMerchant(principal.getName(), pageable, searchTerm, rating, responseStatus));
    }

    @PostMapping("/{reviewId}/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> favoriteReview(@PathVariable Long reviewId, Principal principal) {
        return reviewService.addFavorite(reviewId, principal.getName());
    }

    @DeleteMapping("/{reviewId}/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> unfavoriteReview(@PathVariable Long reviewId, Principal principal) {
        return reviewService.removeFavorite(reviewId, principal.getName());
    }
    
    
    @PostMapping("/{reviewId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> likeReview(@PathVariable Long reviewId, Principal principal) {
        return reviewService.likeReview(reviewId, principal.getName());
    }
    
    @DeleteMapping("/{reviewId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> unlikeReview(@PathVariable Long reviewId, Principal principal) {
        return reviewService.unlikeReview(reviewId, principal.getName());
    }
    
    @GetMapping("/{reviewId}/is-liked")
    public ResponseEntity<Boolean> checkIfReviewIsLiked(@PathVariable Long reviewId, Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(reviewService.checkIfReviewedIsLiked(reviewId, principal.getName()));
    }
    
    @GetMapping("/{reviewId}/favorite/status")
    public ResponseEntity<Boolean> checkIfReviewIsFavorited(@PathVariable Long reviewId, Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(reviewService.checkIfReviewIsFavorited(reviewId, principal.getName()));
    }
    
    
    @GetMapping("/hot")
    public ResponseEntity<Page<ReviewResponse>> getHotReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewService.getHotReviews(pageable));
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<Page<ReviewResponse>> getTopRatedReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewService.getTopRatedReviews(pageable));
    }
    
    
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }
    
    
    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.findAllReviewResponses(pageable));
    }
}