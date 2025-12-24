package com.zpyx.zhongpingyouxuan.controller.admin;

import com.zpyx.zhongpingyouxuan.entity.Comment;
import com.zpyx.zhongpingyouxuan.service.CommentService;
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
@RequestMapping("/api/admin/comments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    
    @GetMapping
    public ResponseEntity<Page<Comment>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(commentService.findAllComments(pageable));
    }

    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Principal principal) {
        return commentService.deleteComment(commentId, principal.getName());
    }
}