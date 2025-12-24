package com.zpyx.zhongpingyouxuan.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String title;
    private String content;
    private String imageUrls;
    private Integer likesCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private Long productId;
    private Boolean isLiked;
    private MerchantReply merchantReply;
    private String responseStatus;
    
    @Data
    public static class MerchantReply {
        private Long id;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }
}