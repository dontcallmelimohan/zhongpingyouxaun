package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.dto.request.ReviewCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.ReviewUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.ReviewResponse;
import com.zpyx.zhongpingyouxuan.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<?> createReview(ReviewCreateRequest reviewCreateRequest, String username);
    Page<ReviewResponse> findReviewsByProductId(Long productId, Pageable pageable);
    Page<ReviewResponse> findReviewsByCurrentUser(String username, Pageable pageable);
    Page<ReviewResponse> findReviewsByMerchant(String username, Pageable pageable);
    Page<ReviewResponse> findReviewsByMerchant(String username, Pageable pageable, String searchTerm, String rating, String responseStatus);
    ResponseEntity<?> addFavorite(Long reviewId, String username);
    ResponseEntity<?> removeFavorite(Long reviewId, String username);
    
    
    ResponseEntity<?> likeReview(Long reviewId, String username);
    ResponseEntity<?> unlikeReview(Long reviewId, String username);
    boolean checkIfReviewedIsLiked(Long reviewId, String username);
    boolean checkIfReviewIsFavorited(Long reviewId, String username);
    
    
    Page<ReviewResponse> getHotReviews(Pageable pageable);
    Page<ReviewResponse> getTopRatedReviews(Pageable pageable);
    
    
    Page<Review> findAllReviews(Pageable pageable);
    
    Page<ReviewResponse> findAllReviewResponses(Pageable pageable);
    Review updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest);
    void deleteReview(Long reviewId);
    Page<ReviewResponse> searchReviews(String keyword, Pageable pageable);
    
    
    ReviewResponse getReviewById(Long reviewId);
}