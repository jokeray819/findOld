package com.findold.controller;

import com.findold.dto.post.PostCreateRequest;
import com.findold.dto.post.PostDetailResponse;
import com.findold.dto.post.PostListItem;
import com.findold.dto.post.PostUpdateRequest;
import com.findold.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDetailResponse create(@Valid @RequestBody PostCreateRequest request) {
        var post = postService.create(request);
        return postService.getById(post.getId());
    }

    @GetMapping("/{id}")
    public PostDetailResponse getById(@PathVariable Long id) {
        return postService.getById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody PostUpdateRequest request) {
        postService.update(id, request);
    }

    @GetMapping
    public Page<PostListItem> list(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String sceneEra,
        @RequestParam(required = false) List<Long> tagIds,
        @RequestParam(required = false) String bountyMode,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postService.list(type, sceneEra, tagIds, bountyMode, pageable);
    }

    @GetMapping("/search")
    public Page<PostListItem> search(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String sceneEra,
        @RequestParam(required = false) List<Long> tagIds,
        @RequestParam(required = false) String bountyMode,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postService.search(q, type, sceneEra, tagIds, bountyMode, pageable);
    }
}
