package com.findold.service;

import com.findold.domain.User;
import com.findold.dto.auth.LoginResponse;
import com.findold.repository.UserRepository;
import com.findold.security.JwtUtil;
import com.findold.security.WechatMiniClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final WechatMiniClient wechatMiniClient;

    @Transactional
    public LoginResponse wechatMiniLogin(String code) {
        WechatMiniClient.Code2SessionResult result = wechatMiniClient.code2Session(code);
        if (result == null || result.getOpenid() == null) {
            throw new IllegalArgumentException("微信登录失败");
        }
        User user = userRepository.findByOpenid(result.getOpenid())
            .orElseGet(() -> {
                User newUser = User.builder()
                    .openid(result.getOpenid())
                    .unionid(result.getUnionid())
                    .nickname("用户" + result.getOpenid().substring(Math.max(0, result.getOpenid().length() - 6)))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
                return userRepository.save(newUser);
            });
        String token = jwtUtil.generateToken(user.getId(), user.getOpenid());
        return LoginResponse.builder()
            .token(token)
            .user(LoginResponse.UserInfo.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .openid(user.getOpenid())
                .build())
            .build();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }
}
