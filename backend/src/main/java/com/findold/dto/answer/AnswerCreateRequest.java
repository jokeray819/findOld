package com.findold.dto.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerCreateRequest {

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    @NotBlank(message = "公开内容不能为空")
    private String contentPublic;

    /** 仅求助者可见：链接/渠道等 */
    private String contentPrivate;
}
