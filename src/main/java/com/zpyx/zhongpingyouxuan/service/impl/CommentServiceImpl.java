package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.dto.request.CommentCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.CommentResponse;
import com.zpyx.zhongpingyouxuan.entity.Comment;
import com.zpyx.zhongpingyouxuan.entity.Merchant;
import com.zpyx.zhongpingyouxuan.entity.Review;
import com.zpyx.zhongpingyouxuan.entity.User;
import com.zpyx.zhongpingyouxuan.exception.ResourceNotFoundException;
import com.zpyx.zhongpingyouxuan.repository.CommentRepository;
import com.zpyx.zhongpingyouxuan.repository.MerchantRepository;
import com.zpyx.zhongpingyouxuan.repository.ReviewRepository;
import com.zpyx.zhongpingyouxuan.repository.UserRepository;
import java.util.stream.Collectors;
import com.zpyx.zhongpingyouxuan.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            MerchantRepository merchantRepository) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> createComment(CommentCreateRequest request, String username) {
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", request.getReviewId()));
        
        
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setReview(review);
        
        
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", request.getParentId()));
            comment.setParent(parent);
        }
        
        
        if (request.getReplyToUserId() != null) {
            User replyToUser = userRepository.findById(request.getReplyToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getReplyToUserId()));
            comment.setReplyToUser(replyToUser);
        }
        
        commentRepository.save(comment);
        
        
        System.out.println("=== 开始检查商家回复状态更新条件 ===");
        System.out.println("评论用户ID: " + user.getId() + ", 用户名: " + user.getUsername());
        System.out.println("用户角色: " + user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList()));
        System.out.println("评论父ID: " + (comment.getParent() != null ? comment.getParent().getId() : "null"));
        System.out.println("请求参数 - reviewId: " + request.getReviewId() + ", parentId: " + request.getParentId());
        System.out.println("评价ID: " + review.getId() + ", 当前回复状态: " + review.getResponseStatus());
        
        
        boolean isMerchantUser = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_MERCHANT"));
        boolean isTopLevelComment = comment.getParent() == null;
        
        System.out.println("是商家用户: " + isMerchantUser);
        System.out.println("是顶级评论: " + isTopLevelComment);
        System.out.println("满足商家回复条件: " + (isMerchantUser && isTopLevelComment));
        
        
        if (isTopLevelComment) {
            System.out.println("=== 强制测试：检测到顶级评论，更新回复状态 ===");
            
            
            Review updatedReview = reviewRepository.findById(review.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", review.getId()));
            
            System.out.println("重新加载的评价对象，回复状态: " + updatedReview.getResponseStatus());
            
            
            updatedReview.setResponseStatus("REPLIED");
            Review savedReview = reviewRepository.save(updatedReview);
            
            System.out.println("商家回复已创建，更新评价ID: " + savedReview.getId() + " 的回复状态为: " + savedReview.getResponseStatus());
            System.out.println("数据库保存成功，验证状态更新...");
            
            
            Review verifiedReview = reviewRepository.findById(review.getId()).orElse(null);
            if (verifiedReview != null) {
                System.out.println("验证结果 - 评价ID: " + verifiedReview.getId() + " 的回复状态: " + verifiedReview.getResponseStatus());
            }
            
            System.out.println("=== 商家回复状态更新完成 ===");
        } else {
            System.out.println("=== 不是顶级评论，跳过状态更新 ===");
            System.out.println("评论类型: " + (comment.getParent() != null ? "回复评论" : "顶级评论"));
        }
        System.out.println("=== 商家回复状态检查结束 ===");
        
        return ResponseEntity.ok(convertToDto(comment));
    }

    @Override
    public Page<CommentResponse> findCommentsByReviewId(Long reviewId, Pageable pageable) {
        return commentRepository.findByReviewIdAndParentIsNull(reviewId, pageable).map(this::convertToDtoWithReplies);
    }

    @Override
    public List<CommentResponse> getRepliesByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        
        return comment.getReplies().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        
        
        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("无权删除此评论!");
        }
        
        commentRepository.delete(comment);
        return ResponseEntity.ok().build();
    }

    @Override
    public Page<Comment> findAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public Page<CommentResponse> findCommentsByUserId(Long userId, Pageable pageable) {
        return commentRepository.findByUserId(userId, pageable).map(this::convertToDto);
    }
    
    
    private CommentResponse convertToDtoWithReplies(Comment comment) {
        CommentResponse dto = convertToDto(comment);
        
        
        List<CommentResponse> replies = comment.getReplies().stream()
                .map(this::convertToDtoWithReplies) 
                .collect(Collectors.toList());
        dto.setReplies(replies);
        
        return dto;
    }
    
    
    private CommentResponse convertToDto(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setReviewId(comment.getReview().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        
        
        if (comment.getReplyToUser() != null) {
            dto.setReplyToUserId(comment.getReplyToUser().getId());
            dto.setReplyToUsername(comment.getReplyToUser().getUsername());
        }
        
        if (comment.getParent() != null) {
            dto.setParentId(comment.getParent().getId());
        }
        
        return dto;
    }
}