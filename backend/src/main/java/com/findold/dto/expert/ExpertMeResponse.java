package com.findold.dto.expert;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ExpertMeResponse {

    private Long userId;
    private Integer validHelpCount;
    private String status;
    private Instant certifiedAt;
}
