package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.dto.request.CommentCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.CommentResponse;
import com.zpyx.zhongpingyouxuan.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface CommentService {
    
    ResponseEntity<?> createComment(CommentCreateRequest commentCreateRequest, String username);
    
    
    Page<CommentResponse> findCommentsByReviewId(Long reviewId, Pageable pageable);
    
    
    List<CommentResponse> getRepliesByCommentId(Long commentId);
    
    
    Page<CommentResponse> findCommentsByUserId(Long userId, Pageable pageable);
    
    
    ResponseEntity<?> deleteComment(Long commentId, String username);
    
    
    Page<Comment> findAllComments(Pageable pageable);
}