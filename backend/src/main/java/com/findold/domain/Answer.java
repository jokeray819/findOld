package com.findold.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "answer", indexes = {
    @Index(columnList = "postId"),
    @Index(columnList = "userId"),
    @Index(columnList = "status"),
    @Index(columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contentPublic;

    /** 仅求助者可见：链接/渠道等 */
    @Column(columnDefinition = "TEXT")
    private String contentPrivate;

    /** 状态: pending / accepted / rejected */
    @Column(nullable = false, length = 16)
    @Builder.Default
    private String status = "pending";

    /** 审核状态预留 */
    @Column(length = 16)
    @Builder.Default
    private String auditStatus = "pass";

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
