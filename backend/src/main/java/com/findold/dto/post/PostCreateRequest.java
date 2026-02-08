package com.findold.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PostCreateRequest {

    /** 物品类型: toy / stationery / snack / old_object */
    @NotBlank(message = "物品类型不能为空")
    private String type;

    @NotBlank(message = "描述不能为空")
    private String description;

    /** 年代: 80s / 90s / 00s */
    private String sceneEra;

    private String regionZone;

    private BigDecimal bountyAmount;

    /** 赏金方式: escrow / post_pay / none */
    private String bountyMode;

    /** 状态: draft / open */
    private String status;

    private String coverImage;

    private String sketchImage;

    private List<Long> tagIds;
}
