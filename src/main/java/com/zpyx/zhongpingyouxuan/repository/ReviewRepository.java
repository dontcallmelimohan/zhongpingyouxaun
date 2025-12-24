package com.zpyx.zhongpingyouxuan.repository;

import com.zpyx.zhongpingyouxuan.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Page<Review> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.merchant.id = :merchantId")
    Page<Review> findByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.merchant.id = :merchantId AND (r.title LIKE %:keyword% OR r.content LIKE %:keyword% OR r.user.username LIKE %:keyword%)")
    Page<Review> searchReviewsByMerchant(@Param("merchantId") Long merchantId, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.merchant.id = :merchantId AND r.rating = :rating")
    Page<Review> findByMerchantIdAndRating(@Param("merchantId") Long merchantId, @Param("rating") Integer rating, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.merchant.id = :merchantId AND r.responseStatus = :responseStatus")
    Page<Review> findByMerchantIdAndResponseStatus(@Param("merchantId") Long merchantId, @Param("responseStatus") String responseStatus, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.title LIKE %:keyword% OR r.content LIKE %:keyword% OR r.user.username LIKE %:keyword%")
    Page<Review> searchReviews(@Param("keyword") String keyword, Pageable pageable);
    
    
    Page<Review> findTopByOrderByLikesCountDesc(Pageable pageable);
    
    
    Page<Review> findTopByOrderByRatingDesc(Pageable pageable);
    
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double calculateAverageRatingByProductId(@Param("productId") Long productId);
    
    
    Integer countByProductId(Long productId);
}