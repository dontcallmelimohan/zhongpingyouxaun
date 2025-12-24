package com.zpyx.zhongpingyouxuan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateRequest {
    
    @NotNull(message = "评价ID不能为空")
    private Long reviewId;
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    private Long parentId;
    
    private Long replyToUserId;
}