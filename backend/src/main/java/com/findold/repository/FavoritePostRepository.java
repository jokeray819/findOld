package com.findold.repository;

import com.findold.domain.FavoritePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {

    Optional<FavoritePost> findByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    Page<FavoritePost> findByUserId(Long userId, Pageable pageable);
}
