package com.zpyx.zhongpingyouxuan.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long reviewId;
    private Long userId;
    private String username;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;
    private List<CommentResponse> replies;
}