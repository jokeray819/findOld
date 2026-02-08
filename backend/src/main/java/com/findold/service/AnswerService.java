package com.findold.service;

import com.findold.domain.Answer;
import com.findold.domain.Post;
import com.findold.dto.answer.AnswerCreateRequest;
import com.findold.dto.answer.AnswerResponse;
import com.findold.repository.AnswerRepository;
import com.findold.repository.PostRepository;
import com.findold.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;
    private final BountyOrderService bountyOrderService;
    private final ExpertService expertService;

    public long countByPostId(Long postId) {
        return answerRepository.countByPostId(postId);
    }

    @Transactional
    public Answer create(Long postId, String contentPublic, String contentPrivate) {
        Long userId = CurrentUser.requireUserId();
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        if (!"open".equals(post.getStatus()) && !"draft".equals(post.getStatus())) {
            throw new IllegalArgumentException("帖子已关闭");
        }
        Answer answer = Answer.builder()
            .postId(postId)
            .userId(userId)
            .contentPublic(contentPublic)
            .contentPrivate(contentPrivate)
            .status("pending")
            .build();
        return answerRepository.save(answer);
    }

    public List<AnswerResponse> listByPostId(Long postId, int page, int size, Long viewerUserId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        boolean isPostOwner = viewerUserId != null && viewerUserId.equals(post.getUserId());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return answerRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable).stream()
            .map(a -> toResponse(a, isPostOwner))
            .collect(Collectors.toList());
    }

    private AnswerResponse toResponse(Answer a, boolean includePrivate) {
        return AnswerResponse.builder()
            .id(a.getId())
            .postId(a.getPostId())
            .userId(a.getUserId())
            .contentPublic(a.getContentPublic())
            .contentPrivate(includePrivate ? a.getContentPrivate() : null)
            .status(a.getStatus())
            .createdAt(a.getCreatedAt())
            .build();
    }

    @Transactional
    public void accept(Long postId, Long answerId) {
        Long userId = CurrentUser.requireUserId();
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("仅楼主可采纳解答");
        }
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new IllegalArgumentException("解答不存在"));
        if (!answer.getPostId().equals(postId)) {
            throw new IllegalArgumentException("解答不属于该帖子");
        }
        if ("accepted".equals(answer.getStatus())) {
            throw new IllegalArgumentException("已采纳过该解答");
        }
        answer.setStatus("accepted");
        answerRepository.save(answer);
        post.setStatus("closed");
        postRepository.save(post);

        bountyOrderService.onAnswerAccepted(postId, answerId, userId);
        expertService.incrementValidHelpCount(answer.getUserId());
    }

    public Answer getById(Long id) {
        return answerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("解答不存在"));
    }

    public org.springframework.data.domain.Page<Answer> listByUserId(Long userId, Pageable pageable) {
        return answerRepository.findByUserId(userId, pageable);
    }
}
