package com.findold.repository;

import com.findold.domain.Expert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpertRepository extends JpaRepository<Expert, Long> {

    Optional<Expert> findByUserId(Long userId);

    Page<Expert> findByStatus(String status, Pageable pageable);
}
