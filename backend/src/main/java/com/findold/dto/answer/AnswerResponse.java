package com.findold.dto.answer;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AnswerResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private String contentPublic;
    /** 仅当请求者为帖子作者时返回 */
    private String contentPrivate;
    private String status;
    private Instant createdAt;
}
