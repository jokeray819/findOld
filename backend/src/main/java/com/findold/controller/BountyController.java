package com.findold.controller;

import com.findold.domain.BountyOrder;
import com.findold.dto.bounty.BountyOrderCreateRequest;
import com.findold.dto.bounty.BountyOrderResponse;
import com.findold.service.BountyOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bounty/orders")
@RequiredArgsConstructor
public class BountyController {

    private final BountyOrderService bountyOrderService;

    @PostMapping
    public BountyOrderResponse create(@Valid @RequestBody BountyOrderCreateRequest request) {
        BountyOrder order = bountyOrderService.create(request.getPostId(), request.getAmount());
        return BountyOrderResponse.builder()
            .id(order.getId())
            .postId(order.getPostId())
            .requesterId(order.getRequesterId())
            .answerId(order.getAnswerId())
            .amount(order.getAmount())
            .status(order.getStatus())
            .paidAt(order.getPaidAt())
            .createdAt(order.getCreatedAt())
            .build();
    }

    @GetMapping
    public Page<BountyOrderResponse> listMyOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return bountyOrderService.listMyOrders(pageable);
    }

    @PostMapping("/{id}/escrow")
    public void escrow(@PathVariable Long id) {
        bountyOrderService.escrow(id);
    }

    @PostMapping("/{id}/confirm-pay")
    public void confirmPay(@PathVariable Long id) {
        bountyOrderService.confirmPay(id);
    }
}
