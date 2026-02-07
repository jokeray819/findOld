package com.findold.dto.bounty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BountyOrderCreateRequest {

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    @NotNull(message = "赏金金额不能为空")
    private BigDecimal amount;
}
