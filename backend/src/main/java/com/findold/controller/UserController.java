package com.findold.controller;

import com.findold.dto.post.PostListItem;
import com.findold.dto.user.UserMeResponse;
import com.findold.security.CurrentUser;
import com.findold.service.AnswerService;
import com.findold.service.BountyOrderService;
import com.findold.service.PostService;
import com.findold.service.UserService;
import com.findold.dto.answer.AnswerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final AnswerService answerService;
    private final BountyOrderService bountyOrderService;

    @GetMapping("/me")
    public UserMeResponse getMe() {
        Long userId = CurrentUser.requireUserId();
        var user = userService.getById(userId);
        long postCount = postService.listByUserId(userId, Pageable.unpaged()).getTotalElements();
        long answerCount = answerService.listByUserId(userId, Pageable.unpaged()).getTotalElements();
        boolean hasBountyOrders = bountyOrderService.listMyOrders(PageRequest.of(0, 1)).hasContent();
        return UserMeResponse.builder()
            .id(user.getId())
            .openid(user.getOpenid())
            .nickname(user.getNickname())
            .avatarUrl(user.getAvatarUrl())
            .phone(user.getPhone())
            .postCount(postCount)
            .answerCount(answerCount)
            .hasBountyOrders(hasBountyOrders)
            .build();
    }

    @GetMapping("/me/posts")
    public Page<PostListItem> getMyPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = CurrentUser.requireUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postService.listByUserId(userId, pageable);
    }

    @GetMapping("/me/answers")
    public Page<AnswerResponse> getMyAnswers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = CurrentUser.requireUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return answerService.listByUserId(userId, pageable)
            .map(a -> AnswerResponse.builder()
                .id(a.getId())
                .postId(a.getPostId())
                .userId(a.getUserId())
                .contentPublic(a.getContentPublic())
                .contentPrivate(null)
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build());
    }
}
