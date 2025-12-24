package com.zpyx.zhongpingyouxuan.repository;

import com.zpyx.zhongpingyouxuan.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    Page<Comment> findByReviewIdAndParentIsNull(Long reviewId, Pageable pageable);
    
    
    Page<Comment> findByUserId(Long userId, Pageable pageable);
    
    
    List<Comment> findByParentId(Long parentId);
    
    
    @Query("SELECT c FROM Comment c WHERE c.review.id = :reviewId ORDER BY c.createdAt DESC")
    List<Comment> findAllByReviewId(@Param("reviewId") Long reviewId);
}