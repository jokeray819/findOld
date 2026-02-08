package com.findold.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post", indexes = {
    @Index(columnList = "userId"),
    @Index(columnList = "status"),
    @Index(columnList = "type"),
    @Index(columnList = "sceneEra"),
    @Index(columnList = "bountyMode"),
    @Index(columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    /** 物品类型: toy / stationery / snack / old_object */
    @Column(nullable = false, length = 32)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** 年代: 80s / 90s / 00s */
    @Column(length = 16)
    private String sceneEra;

    @Column(length = 64)
    private String regionZone;

    @Column(precision = 10, scale = 2)
    private BigDecimal bountyAmount;

    /** 赏金方式: escrow / post_pay / none */
    @Column(length = 16)
    private String bountyMode;

    /** 状态: draft / open / closed */
    @Column(nullable = false, length = 16)
    @Builder.Default
    private String status = "open";

    @Column(length = 512)
    private String coverImage;

    @Column(length = 512)
    private String sketchImage;

    /** 审核状态预留: pending / pass / reject */
    @Column(length = 16)
    @Builder.Default
    private String auditStatus = "pass";

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostTag> postTags = new ArrayList<>();

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
