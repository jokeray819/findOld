package com.findold.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {}

    public static Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }

    public static Long requireUserId() {
        Long id = getUserId();
        if (id == null) {
            throw new com.findold.exception.UnauthorizedException("未登录");
        }
        return id;
    }
}
