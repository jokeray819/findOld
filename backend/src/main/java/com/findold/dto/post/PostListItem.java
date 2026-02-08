package com.findold.dto.post;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PostListItem {

    private Long id;
    private Long userId;
    private String type;
    private String description;
    private String sceneEra;
    private BigDecimal bountyAmount;
    private String bountyMode;
    private String status;
    private String coverImage;
    private List<PostDetailResponse.TagDto> tags;
    private long answerCount;
    private Instant createdAt;
}
