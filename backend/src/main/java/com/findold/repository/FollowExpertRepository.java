package com.findold.repository;

import com.findold.domain.FollowExpert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowExpertRepository extends JpaRepository<FollowExpert, Long> {

    Optional<FollowExpert> findByUserIdAndExpertUserId(Long userId, Long expertUserId);

    boolean existsByUserIdAndExpertUserId(Long userId, Long expertUserId);

    void deleteByUserIdAndExpertUserId(Long userId, Long expertUserId);

    Page<FollowExpert> findByUserId(Long userId, Pageable pageable);
}
