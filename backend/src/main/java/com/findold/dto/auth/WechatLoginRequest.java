package com.findold.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WechatLoginRequest {

    @NotBlank(message = "code 不能为空")
    private String code;
}
