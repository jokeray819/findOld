package com.findold.controller;

import com.findold.dto.auth.LoginResponse;
import com.findold.dto.auth.WechatLoginRequest;
import com.findold.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/wechat/mini-login")
    public LoginResponse wechatMiniLogin(@Valid @RequestBody WechatLoginRequest request) {
        return userService.wechatMiniLogin(request.getCode());
    }
}
