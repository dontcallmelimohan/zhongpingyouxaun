package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.dto.request.ReviewCreateRequest;
import com.zpyx.zhongpingyouxuan.dto.request.ReviewUpdateRequest;
import com.zpyx.zhongpingyouxuan.dto.response.MessageResponse;
import com.zpyx.zhongpingyouxuan.dto.response.ReviewResponse;
import com.zpyx.zhongpingyouxuan.entity.*;
import com.zpyx.zhongpingyouxuan.exception.ResourceNotFoundException;
import com.zpyx.zhongpingyouxuan.exception.UnauthorizedException;
import com.zpyx.zhongpingyouxuan.repository.CommentRepository;
import com.zpyx.zhongpingyouxuan.repository.FavoriteRepository;
import com.zpyx.zhongpingyouxuan.repository.LikeRepository;
import com.zpyx.zhongpingyouxuan.repository.MerchantRepository;
import com.zpyx.zhongpingyouxuan.repository.ProductRepository;
import com.zpyx.zhongpingyouxuan.repository.ReviewRepository;
import com.zpyx.zhongpingyouxuan.repository.UserRepository;
import com.zpyx.zhongpingyouxuan.service.ProductService;
import com.zpyx.zhongpingyouxuan.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    private final LikeRepository likeRepository;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final ProductService productService;

    
    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            FavoriteRepository favoriteRepository,
            LikeRepository likeRepository,
            MerchantRepository merchantRepository,
            CommentRepository commentRepository,
            ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.likeRepository = likeRepository;
        this.merchantRepository = merchantRepository;
        this.commentRepository = commentRepository;
        this.productService = productService;
    }

    @Override
    @Transactional
    public ResponseEntity<?> createReview(ReviewCreateRequest req, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", req.getProductId()));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(req.getRating());
        review.setTitle(req.getTitle());
        review.setContent(req.getContent());
        review.setImageUrls(req.getImageUrls());

        reviewRepository.save(review);
        
        productService.updateProductRating(product.getId());
        return ResponseEntity.ok(new MessageResponse("评价发布成功!"));
    }
    
    private ReviewResponse convertToDto(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setContent(review.getContent());
        dto.setImageUrls(review.getImageUrls());
        dto.setLikesCount(review.getLikesCount());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setProductId(review.getProduct().getId());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        
        
        dto.setResponseStatus(review.getResponseStatus());
        
        
        List<Comment> allComments = commentRepository.findAllByReviewId(review.getId());
        for (Comment comment : allComments) {
            
            if (comment.getParent() == null && comment.getUser().getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_MERCHANT"))) {
                
                
                Long productMerchantId = review.getProduct().getMerchant() != null ? 
                    review.getProduct().getMerchant().getId() : null;
                
                
                Optional<Merchant> commentUserMerchant = merchantRepository.findByOwnerId(comment.getUser().getId());
                
                
                if (productMerchantId != null && commentUserMerchant.isPresent() && 
                    productMerchantId.equals(commentUserMerchant.get().getId())) {
                    
                    ReviewResponse.MerchantReply merchantReply = new ReviewResponse.MerchantReply();
                    merchantReply.setId(comment.getId());
                    merchantReply.setContent(comment.getContent());
                    merchantReply.setCreatedAt(comment.getCreatedAt());
                    dto.setMerchantReply(merchantReply);
                    break; 
                }
            }
        }
        
        return dto;
    }

    @Override
    public Page<ReviewResponse> findReviewsByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ReviewResponse> findReviewsByCurrentUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return reviewRepository.findByUserId(user.getId(), pageable).map(this::convertToDto);
    }

    @Override
    public Page<ReviewResponse> findReviewsByMerchant(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        
        Merchant merchant = merchantRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a merchant"));
        
        
        return reviewRepository.findByMerchantId(merchant.getId(), pageable).map(this::convertToDto);
    }
    
    @Override
    public Page<ReviewResponse> findReviewsByMerchant(String username, Pageable pageable, String searchTerm, String rating, String responseStatus) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Merchant merchant = merchantRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a merchant"));
        
        // 如果有搜索关键词，使用搜索功能
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return reviewRepository.searchReviewsByMerchant(merchant.getId(), searchTerm, pageable).map(this::convertToDto);
        }
        
        // 如果有评分筛选，使用评分筛选功能
        if (rating != null && !rating.isEmpty()) {
            return reviewRepository.findByMerchantIdAndRating(merchant.getId(), Integer.parseInt(rating), pageable).map(this::convertToDto);
        }
        
        // 如果有回复状态筛选，使用回复状态筛选功能
        if (responseStatus != null && !responseStatus.isEmpty()) {
            return reviewRepository.findByMerchantIdAndResponseStatus(merchant.getId(), responseStatus, pageable).map(this::convertToDto);
        }
        
        // 如果没有筛选条件，返回所有评价
        return reviewRepository.findByMerchantId(merchant.getId(), pageable).map(this::convertToDto);
    }

    @Override
    @Transactional
    public ResponseEntity<?> addFavorite(Long reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (favoriteRepository.findByUserIdAndReviewId(user.getId(), review.getId()).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("已收藏该评价"));
        }

        Favorite favorite = new Favorite(user, review);
        favoriteRepository.save(favorite);

        return ResponseEntity.ok(new MessageResponse("收藏成功"));
    }

    @Override
    @Transactional
    public ResponseEntity<?> removeFavorite(Long reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        
        
        Favorite favorite = favoriteRepository.findByUserIdAndReviewId(user.getId(), reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite", "userId and reviewId", user.getId() + " & " + reviewId));

        favoriteRepository.delete(favorite);
        return ResponseEntity.ok(new MessageResponse("取消收藏成功"));
    }
    
    
    @Override
    public Page<Review> findAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }
    
    @Override
    public Page<ReviewResponse> findAllReviewResponses(Pageable pageable) {
        
        return reviewRepository.findAll(pageable).map(this::convertToDto);
    }
    
    @Override
    public Page<ReviewResponse> searchReviews(String keyword, Pageable pageable) {
        return reviewRepository.searchReviews(keyword, pageable).map(this::convertToDto);
    }
    
    @Override
    public Review updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        
        review.setRating(reviewUpdateRequest.getRating());
        review.setTitle(reviewUpdateRequest.getTitle());
        review.setContent(reviewUpdateRequest.getContent());
        review.setImageUrls(reviewUpdateRequest.getImageUrls());
        
        Review savedReview = reviewRepository.save(review);
        
        productService.updateProductRating(savedReview.getProduct().getId());
        return savedReview;
    }
    
    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        
        productService.updateProductRating(productId);
    }
    
    
    @Override
    @Transactional
    public ResponseEntity<?> likeReview(Long reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        
        if (likeRepository.existsByUserIdAndReviewId(user.getId(), reviewId)) {
            return ResponseEntity.badRequest().body(new MessageResponse("已点赞该评价"));
        }
        
        
        Like like = new Like(user, review);
        likeRepository.save(like);
        
        
        Review refreshedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        
        long actualLikesCount = likeRepository.countByReviewId(reviewId);
        refreshedReview.setLikesCount((int) actualLikesCount);
        reviewRepository.save(refreshedReview);
        
        return ResponseEntity.ok(new MessageResponse("点赞成功"));
    }
    
    
    @Override
    @Transactional
    public ResponseEntity<?> unlikeReview(Long reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        
        if (!likeRepository.existsByUserIdAndReviewId(user.getId(), reviewId)) {
            return ResponseEntity.badRequest().body(new MessageResponse("未点赞该评价"));
        }
        
        
        likeRepository.deleteByUserIdAndReviewId(user.getId(), reviewId);
        
        
        Review refreshedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        
        long actualLikesCount = likeRepository.countByReviewId(reviewId);
        refreshedReview.setLikesCount((int) actualLikesCount);
        reviewRepository.save(refreshedReview);
        
        return ResponseEntity.ok(new MessageResponse("取消点赞成功"));
    }
    
    
    @Override
    public boolean checkIfReviewedIsLiked(Long reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return likeRepository.existsByUserIdAndReviewId(user.getId(), reviewId);
    }
    
    
    @Override
    public boolean checkIfReviewIsFavorited(Long reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return favoriteRepository.findByUserIdAndReviewId(user.getId(), reviewId).isPresent();
    }
    
    
    @Override
    public Page<ReviewResponse> getHotReviews(Pageable pageable) {
        return reviewRepository.findTopByOrderByLikesCountDesc(pageable).map(this::convertToDto);
    }
    
    
    @Override
    public Page<ReviewResponse> getTopRatedReviews(Pageable pageable) {
        return reviewRepository.findTopByOrderByRatingDesc(pageable).map(this::convertToDto);
    }
    
    
    @Override
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        return convertToDto(review);
    }
}