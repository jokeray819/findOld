package com.findold.controller;

import com.findold.dto.post.PostDetailResponse;
import com.findold.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/posts/{postId}")
    public void add(@PathVariable Long postId) {
        favoriteService.add(postId);
    }

    @DeleteMapping("/posts/{postId}")
    public void remove(@PathVariable Long postId) {
        favoriteService.remove(postId);
    }

    @GetMapping("/posts")
    public Page<PostDetailResponse> listMyFavorites(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return favoriteService.listMyFavorites(pageable);
    }
}
