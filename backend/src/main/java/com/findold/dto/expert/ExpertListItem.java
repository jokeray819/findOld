package com.findold.dto.expert;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ExpertListItem {

    private Long userId;
    private String nickname;
    private String avatarUrl;
    private Integer validHelpCount;
    private String status;
    private Instant certifiedAt;
}
