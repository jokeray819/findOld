package com.findold.controller;

import com.findold.domain.Answer;
import com.findold.dto.answer.AnswerCreateRequest;
import com.findold.dto.answer.AnswerResponse;
import com.findold.security.CurrentUser;
import com.findold.service.AnswerService;
import com.findold.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final PostService postService;

    @PostMapping("/{postId}/answers")
    public AnswerResponse createAnswer(@PathVariable Long postId, @Valid @RequestBody AnswerCreateRequest request) {
        if (!postId.equals(request.getPostId())) {
            throw new IllegalArgumentException("帖子ID不一致");
        }
        Answer answer = answerService.create(postId, request.getContentPublic(), request.getContentPrivate());
        var post = postService.getPostEntity(postId);
        boolean isOwner = CurrentUser.getUserId().equals(post.getUserId());
        return AnswerResponse.builder()
            .id(answer.getId())
            .postId(answer.getPostId())
            .userId(answer.getUserId())
            .contentPublic(answer.getContentPublic())
            .contentPrivate(isOwner ? answer.getContentPrivate() : null)
            .status(answer.getStatus())
            .createdAt(answer.getCreatedAt())
            .build();
    }

    @GetMapping("/{postId}/answers")
    public List<AnswerResponse> listAnswers(
        @PathVariable Long postId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Long viewerId = CurrentUser.getUserId();
        return answerService.listByPostId(postId, page, size, viewerId);
    }

    @PostMapping("/{postId}/answers/{answerId}/accept")
    public void acceptAnswer(@PathVariable Long postId, @PathVariable Long answerId) {
        answerService.accept(postId, answerId);
    }
}
