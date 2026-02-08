package com.findold.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "findold-mvp-secret-change-in-production-min-256bits";
    private long expirationMs = 604_800_000L; // 7 days
}
