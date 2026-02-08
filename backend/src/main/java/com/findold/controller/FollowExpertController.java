package com.findold.controller;

import com.findold.dto.expert.ExpertListItem;
import com.findold.service.FollowExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowExpertController {

    private final FollowExpertService followExpertService;

    @PostMapping("/experts/{userId}")
    public void follow(@PathVariable Long userId) {
        followExpertService.follow(userId);
    }

    @DeleteMapping("/experts/{userId}")
    public void unfollow(@PathVariable Long userId) {
        followExpertService.unfollow(userId);
    }

    @GetMapping("/experts")
    public Page<ExpertListItem> listMyFollowings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return followExpertService.listMyFollowings(pageable);
    }
}
