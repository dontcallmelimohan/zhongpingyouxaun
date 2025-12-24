package com.zpyx.zhongpingyouxuan.controller;

import com.zpyx.zhongpingyouxuan.dto.request.CommentCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.CommentResponse;
import com.zpyx.zhongpingyouxuan.service.CommentService;
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
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByReview(
            @PathVariable Long reviewId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(commentService.findCommentsByReviewId(reviewId, pageable));
    }

    
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getRepliesByComment(
            @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getRepliesByCommentId(commentId));
    }

    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateRequest request, Principal principal) {
        String username = principal.getName();
        return commentService.createComment(request, username);
    }

    
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Principal principal) {
        String username = principal.getName();
        return commentService.deleteComment(commentId, username);
    }

    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(commentService.findCommentsByUserId(userId, pageable));
    }


}