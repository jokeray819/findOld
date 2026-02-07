package com.findold.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bounty_order", indexes = {
    @Index(columnList = "postId"),
    @Index(columnList = "requesterId"),
    @Index(columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BountyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long requesterId;

    /** 采纳后填写 */
    @Column
    private Long answerId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /** 状态: created / escrowed / paid / refunded / disputed */
    @Column(nullable = false, length = 16)
    @Builder.Default
    private String status = "created";

    @Column
    private Instant paidAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
