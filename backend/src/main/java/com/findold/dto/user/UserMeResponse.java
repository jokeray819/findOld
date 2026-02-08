package com.findold.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMeResponse {

    private Long id;
    private String openid;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private long postCount;
    private long answerCount;
    private boolean hasBountyOrders;
}
