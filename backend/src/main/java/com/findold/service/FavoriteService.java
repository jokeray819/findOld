package com.findold.service;

import com.findold.domain.FavoritePost;
import com.findold.dto.post.PostDetailResponse;
import com.findold.repository.FavoritePostRepository;
import com.findold.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoritePostRepository favoritePostRepository;
    private final PostService postService;

    @Transactional
    public void add(Long postId) {
        Long userId = CurrentUser.requireUserId();
        if (favoritePostRepository.existsByUserIdAndPostId(userId, postId)) {
            return;
        }
        FavoritePost fp = FavoritePost.builder().userId(userId).postId(postId).build();
        favoritePostRepository.save(fp);
    }

    @Transactional
    public void remove(Long postId) {
        Long userId = CurrentUser.requireUserId();
        favoritePostRepository.deleteByUserIdAndPostId(userId, postId);
    }

    public boolean isFavorited(Long userId, Long postId) {
        return favoritePostRepository.existsByUserIdAndPostId(userId, postId);
    }

    public Page<PostDetailResponse> listMyFavorites(Pageable pageable) {
        Long userId = CurrentUser.requireUserId();
        return favoritePostRepository.findByUserId(userId, pageable)
            .map(fp -> postService.getById(fp.getPostId()));
    }
}
