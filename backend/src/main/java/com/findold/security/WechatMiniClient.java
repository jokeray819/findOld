package com.findold.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * 微信小程序 code2session，无 appId/secret 时返回 mock openid 便于本地开发。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WechatMiniClient {

    private static final String URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    @Value("${wechat.mini.app-id:}")
    private String appId;

    @Value("${wechat.mini.secret:}")
    private String secret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Code2SessionResult code2Session(String code) {
        if (appId == null || appId.isBlank() || secret == null || secret.isBlank()) {
            log.warn("WeChat appId/secret not configured, returning mock openid");
            return Code2SessionResult.builder()
                .openid("mock_openid_" + (code != null ? code : System.currentTimeMillis()))
                .unionid(null)
                .sessionKey(null)
                .build();
        }
        try {
            String url = String.format(URL, appId, secret, code);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(response.body());
            if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                log.error("WeChat code2session error: {}", response.body());
                return null;
            }
            return Code2SessionResult.builder()
                .openid(node.has("openid") ? node.get("openid").asText() : null)
                .unionid(node.has("unionid") ? node.get("unionid").asText() : null)
                .sessionKey(node.has("session_key") ? node.get("session_key").asText() : null)
                .build();
        } catch (Exception e) {
            log.error("WeChat code2session request failed", e);
            return null;
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class Code2SessionResult {
        private String openid;
        private String unionid;
        private String sessionKey;
    }
}
