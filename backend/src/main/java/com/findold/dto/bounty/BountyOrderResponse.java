package com.findold.dto.bounty;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class BountyOrderResponse {

    private Long id;
    private Long postId;
    private Long requesterId;
    private Long answerId;
    private BigDecimal amount;
    private String status;
    private Instant paidAt;
    private Instant createdAt;
}
