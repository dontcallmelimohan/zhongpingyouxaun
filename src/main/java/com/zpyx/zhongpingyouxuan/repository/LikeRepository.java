package com.zpyx.zhongpingyouxuan.repository;

import com.zpyx.zhongpingyouxuan.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    
    Optional<Like> findByUserIdAndReviewId(Long userId, Long reviewId);
    
    
    long countByReviewId(Long reviewId);
    
    
    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);
    
    
    void deleteByUserIdAndReviewId(Long userId, Long reviewId);
}