package com.zpyx.zhongpingyouxuan.controller;

import com.zpyx.zhongpingyouxuan.dto.request.UserProfileUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.CommentResponse;
import com.zpyx.zhongpingyouxuan.dto.response.ProductDetailResponse;
import com.zpyx.zhongpingyouxuan.dto.response.ReviewResponse;
import com.zpyx.zhongpingyouxuan.entity.Comment;
import com.zpyx.zhongpingyouxuan.entity.Favorite;
import com.zpyx.zhongpingyouxuan.entity.Product;
import com.zpyx.zhongpingyouxuan.entity.ProductFavorite;
import com.zpyx.zhongpingyouxuan.entity.Review;
import com.zpyx.zhongpingyouxuan.entity.User;
import com.zpyx.zhongpingyouxuan.exception.ResourceNotFoundException;
import com.zpyx.zhongpingyouxuan.repository.CommentRepository;
import com.zpyx.zhongpingyouxuan.repository.FavoriteRepository;
import com.zpyx.zhongpingyouxuan.repository.ProductFavoriteRepository;
import com.zpyx.zhongpingyouxuan.repository.ProductRepository;
import com.zpyx.zhongpingyouxuan.repository.ReviewRepository;
import com.zpyx.zhongpingyouxuan.repository.UserRepository;
import com.zpyx.zhongpingyouxuan.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private ProductFavoriteRepository productFavoriteRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        return ResponseEntity.ok(userService.findUserProfileByUsername(principal.getName()));
    }
    
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUserProfile(Principal principal, @RequestBody UserProfileUpdateRequest userProfileUpdateRequest) {
        return ResponseEntity.ok(userService.updateUserProfile(principal.getName(), userProfileUpdateRequest));
    }
    
    
    @GetMapping("/favorites/reviews")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserFavoriteReviews(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteRepository.findByUserId(user.getId(), pageable);
        
        Page<ReviewResponse> reviewResponses = favorites.map(favorite -> {
            Review review = reviewRepository.findById(favorite.getReview().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", favorite.getReview().getId()));
            
            
            ReviewResponse response = new ReviewResponse();
            response.setId(review.getId());
            response.setRating(review.getRating());
            response.setTitle(review.getTitle());
            response.setContent(review.getContent());
            response.setImageUrls(review.getImageUrls());
            response.setLikesCount(review.getLikesCount());
            response.setCreatedAt(review.getCreatedAt());
            response.setProductId(review.getProduct().getId());
            response.setUserId(review.getUser().getId());
            response.setUsername(review.getUser().getUsername());
            
            return response;
        });
        
        return ResponseEntity.ok(reviewResponses);
    }
    
    
    @GetMapping("/favorites/products")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserFavoriteProducts(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductFavorite> favorites = productFavoriteRepository.findByUserId(user.getId(), pageable);
        
        Page<ProductDetailResponse> productResponses = favorites.map(favorite -> {
            Product product = productRepository.findById(favorite.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", favorite.getProduct().getId()));
            return modelMapper.map(product, ProductDetailResponse.class);
        });
        
        return ResponseEntity.ok(productResponses);
    }
    
    
    @GetMapping("/replies")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserReplies(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByUserId(user.getId(), pageable);
        
        
        Page<?> formattedResponses = comments.map(comment -> {
            
            Review review = reviewRepository.findById(comment.getReview().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", comment.getReview().getId()));
            
            
            return Map.of(
                "reviewProductId", review.getProduct().getId(),
                "reviewTitle", review.getTitle(),
                "content", comment.getContent(),
                "createdAt", comment.getCreatedAt()
            );
        });
        
        return ResponseEntity.ok(formattedResponses);
    }
    
    
    @GetMapping("/replies/received")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> getRepliesToUser(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        
        
        Page<Comment> userComments = commentRepository.findByUserId(user.getId(), Pageable.unpaged());
        List<Long> userCommentIds = userComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
        
        
        if (userCommentIds.isEmpty()) {
            Pageable emptyPageable = PageRequest.of(page, size);
            return ResponseEntity.ok(Page.empty(emptyPageable));
        }
        
        
        List<Comment> replies = userCommentIds.stream()
                .flatMap(commentId -> commentRepository.findByParentId(commentId).stream())
                .collect(Collectors.toList());
        
        
        int start = Math.min((int) (page * size), replies.size());
        int end = Math.min(start + size, replies.size());
        List<Comment> pagedReplies = replies.subList(start, end);
        
        
        List<?> formattedReplies = pagedReplies.stream().map(comment -> {
            
            Review review = reviewRepository.findById(comment.getReview().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", comment.getReview().getId()));
            
            
            return Map.of(
                "replierUsername", comment.getUser().getUsername(),
                "content", comment.getContent(),
                "createdAt", comment.getCreatedAt(),
                "reviewProductId", review.getProduct().getId(),
                "reviewId", review.getId()
            );
        }).collect(Collectors.toList());
        
        Page<?> pageableReplies = new org.springframework.data.domain.PageImpl<>(
                formattedReplies,
                PageRequest.of(page, size),
                replies.size()
        );
        
        return ResponseEntity.ok(pageableReplies);
    }
    
    
    @GetMapping("/favorites")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserAllFavorites(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {
        
        if ("product".equals(type)) {
            ResponseEntity<?> response = getUserFavoriteProducts(principal, page, size);
            
            if (response.getBody() instanceof Page) {
                Page<?> pageData = (Page<?>) response.getBody();
                return ResponseEntity.ok(pageData.getContent());
            }
            return response;
        } else {
            
            ResponseEntity<?> response = getUserFavoriteReviews(principal, page, size);
            
            if (response.getBody() instanceof Page) {
                Page<?> pageData = (Page<?>) response.getBody();
                return ResponseEntity.ok(pageData.getContent());
            }
            return response;
        }
    }
    
    @GetMapping("/reviews/replies")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserReviewsReplies(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ResponseEntity<?> response = getUserReplies(principal, page, size);
        if (response.getBody() instanceof Page) {
            Page<?> pageData = (Page<?>) response.getBody();
            return ResponseEntity.ok(pageData.getContent());
        }
        return response;
    }
    
    @GetMapping("/reviews/replies-to-me")
    @PreAuthorize("hasRole('USER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserReviewsRepliesToMe(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ResponseEntity<?> response = getRepliesToUser(principal, page, size);
        if (response.getBody() instanceof Page) {
            Page<?> pageData = (Page<?>) response.getBody();
            return ResponseEntity.ok(pageData.getContent());
        }
        return response;
    }
}