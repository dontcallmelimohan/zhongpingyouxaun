package com.zpyx.zhongpingyouxuan.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewCreateRequest {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String imageUrls;
}