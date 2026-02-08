package com.findold.service;

import com.findold.domain.BountyOrder;
import com.findold.domain.Post;
import com.findold.dto.bounty.BountyOrderResponse;
import com.findold.repository.AnswerRepository;
import com.findold.repository.BountyOrderRepository;
import com.findold.repository.PostRepository;
import com.findold.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BountyOrderService {

    private final BountyOrderRepository bountyOrderRepository;
    private final PostRepository postRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public BountyOrder create(Long postId, BigDecimal amount) {
        Long userId = CurrentUser.requireUserId();
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("仅楼主可创建赏金订单");
        }
        if (bountyOrderRepository.findByPostId(postId).isPresent()) {
            throw new IllegalArgumentException("该帖子已有赏金订单");
        }
        BountyOrder order = BountyOrder.builder()
            .postId(postId)
            .requesterId(userId)
            .amount(amount)
            .status("created")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        order = bountyOrderRepository.save(order);
        return order;
    }

    @Transactional
    public void escrow(Long orderId) {
        Long userId = CurrentUser.requireUserId();
        BountyOrder order = bountyOrderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!order.getRequesterId().equals(userId)) {
            throw new IllegalArgumentException("仅求助者可托管赏金");
        }
        if (!"created".equals(order.getStatus())) {
            throw new IllegalArgumentException("当前状态不可托管");
        }
        order.setStatus("escrowed");
        bountyOrderRepository.save(order);
    }

    @Transactional
    public void confirmPay(Long orderId) {
        Long userId = CurrentUser.requireUserId();
        BountyOrder order = bountyOrderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!order.getRequesterId().equals(userId)) {
            throw new IllegalArgumentException("仅求助者可确认打款");
        }
        if ("paid".equals(order.getStatus())) {
            throw new IllegalArgumentException("已打款");
        }
        if ("escrowed".equals(order.getStatus())) {
            order.setStatus("paid");
            order.setPaidAt(Instant.now());
        } else         if ("created".equals(order.getStatus())) {
            var accepted = answerRepository.findByPostIdOrderByCreatedAtDesc(order.getPostId(), Pageable.unpaged()).stream()
                .filter(a -> "accepted".equals(a.getStatus())).findFirst();
            accepted.ifPresent(a -> {
                order.setAnswerId(a.getId());
                order.setStatus("paid");
                order.setPaidAt(Instant.now());
            });
            if (order.getAnswerId() == null) {
                throw new IllegalArgumentException("请先采纳解答后再确认打款");
            }
        }
        bountyOrderRepository.save(order);
    }

    @Transactional
    public void onAnswerAccepted(Long postId, Long answerId, Long requesterId) {
        bountyOrderRepository.findByPostId(postId).ifPresent(order -> {
            if ("escrowed".equals(order.getStatus())) {
                order.setAnswerId(answerId);
                order.setStatus("paid");
                order.setPaidAt(Instant.now());
                bountyOrderRepository.save(order);
            }
        });
    }

    public Page<BountyOrderResponse> listMyOrders(Pageable pageable) {
        Long userId = CurrentUser.requireUserId();
        return bountyOrderRepository.findByRequesterId(userId, pageable).map(this::toResponse);
    }

    private BountyOrderResponse toResponse(BountyOrder o) {
        return BountyOrderResponse.builder()
            .id(o.getId())
            .postId(o.getPostId())
            .requesterId(o.getRequesterId())
            .answerId(o.getAnswerId())
            .amount(o.getAmount())
            .status(o.getStatus())
            .paidAt(o.getPaidAt())
            .createdAt(o.getCreatedAt())
            .build();
    }
}
