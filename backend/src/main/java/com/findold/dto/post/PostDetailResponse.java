package com.findold.dto.post;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PostDetailResponse {

    private Long id;
    private Long userId;
    private String type;
    private String description;
    private String sceneEra;
    private String regionZone;
    private BigDecimal bountyAmount;
    private String bountyMode;
    private String status;
    private String coverImage;
    private String sketchImage;
    private List<TagDto> tags;
    private long answerCount;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    public static class TagDto {
        private Long id;
        private String name;
        private String category;
    }
}
