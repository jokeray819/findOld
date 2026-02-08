package com.findold.repository;

import com.findold.domain.BountyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BountyOrderRepository extends JpaRepository<BountyOrder, Long> {

    Optional<BountyOrder> findByPostId(Long postId);

    Page<BountyOrder> findByRequesterId(Long requesterId, Pageable pageable);

    List<BountyOrder> findByAnswerIdIn(List<Long> answerIds);
}
