package com.findold.dto.post;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PostUpdateRequest {

    private String type;
    private String description;
    private String sceneEra;
    private String regionZone;
    private BigDecimal bountyAmount;
    private String bountyMode;
    private String status;
    private String coverImage;
    private String sketchImage;
    private List<Long> tagIds;
}
